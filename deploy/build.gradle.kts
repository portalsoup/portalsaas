import com.github.jknack.handlebars.Handlebars
import java.io.ByteArrayOutputStream

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.github.jknack:handlebars:4.3.1")
    }
}

plugins {

}

repositories {
    mavenCentral()
}

// Variables required from gradle.properties
val priceChartingKey: String by project
val doToken: String? by project
val doSshId: String? by project
val discordToken: String? by project
val pricechartingToken: String? by project

// Shortcuts
val ansibleDir = "$rootDir/infrastructure/ansible/"
val terraformDir = "$rootDir/infrastructure/terraform/"
val pathToAnsibleInventory = "$ansibleDir/inventory/"
val pathToResources = "$rootDir/src/main/resources/"

tasks {
    rootProject.tasks.getByName("compileKotlin") {
        mustRunAfter("deploy:ktor-config")
    }

    rootProject.tasks.getByName("build") {
        dependsOn(":deploy:ktor-config")
    }

    register<Delete>("clean") {
        delete(
            "\"$ansibleDir/inventory\"",
            "\"$pathToResources/application.conf\""
        )
    }

    register("deploy") {
        dependsOn("terraform-apply", "ansible")
        group = "deploy"
    }

    register("terraform-init") {
        group = "deploy"

        onlyIf {
            logger.info("Checking for the presence of infrastructure/terraform/data.tf")
            File("$terraformDir/data.tf").exists()
        }

        doLast {
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "init",
                    "-var", "do_token=$doToken"
                )
            }
        }
    }


    /*
     * By depending on this task, you require a user to manually validate the calculated diff.  If rejected this task
     * prevents downstream tasks from performing any changes
     */
    register("terraform-plan") {
        group = "deploy"

        onlyIf {
            logger.info("Checking for the presence of infrastructure/terraform/data.tf")
            File("$terraformDir/data.tf").exists()
        }

        // configure stdin for prompts
        rootProject.tasks.getByName<JavaExec>("run") {
            standardInput = System.`in`
        }

        doLast {
            val planResult = project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "plan",
                    "-var", "ssh_id=${doSshId}",
                    "-var", "do_token=$doToken"
                )
            }
            when (planResult.exitValue) {
                0 -> {
                    logger.info("Accept this plan? (Type 'yes' to accept)")
                    readLine()
                        ?.takeIf { it == "yes" }
                        ?: throw GradleException("Terraform planned changes were rejected")
                }
                else -> throw GradleException("Unexpected failure $planResult")
            }
        }
    }

    register("terraform-apply") {
        group = "deploy"

        dependsOn("terraform-plan")

        onlyIf {
            logger.info("Checking for the presence of infrastructure/terraform/data.tf")
            File("$terraformDir/data.tf").exists()
        }

        doLast {
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "apply",
                    "-auto-approve",
                    "-var", "ssh_id=${doSshId}",
                    "-var", "do_token=$doToken"
                )
            }
        }
    }

    register("terraform-db-output") {
        group = "deploy"

        @Suppress("UnstableApiUsage")
        doNotTrackState("We should not cache secrets")

        onlyIf {
            File("$terraformDir/terraform.tfstate").exists()
        }

        val databaseStream = ByteArrayOutputStream()
        val hostStream = ByteArrayOutputStream()
        val portStream = ByteArrayOutputStream()
        val privatehostStream = ByteArrayOutputStream()
        val idStream = ByteArrayOutputStream()
        val userStream = ByteArrayOutputStream()
        val passwordStream = ByteArrayOutputStream()

        doFirst {
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "output", "postgres-database")
                standardOutput = databaseStream
            }
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "output", "postgres-host")
                standardOutput = hostStream
            }
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "output", "postgres-port")
                standardOutput = portStream
            }
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "output", "postgres-private_host")
                standardOutput = privatehostStream
            }.exitValue
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "output", "postgres-id")
                standardOutput = idStream
            }
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "output", "postgres-user")
                standardOutput = userStream
            }
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "output", "postgres-password")
                standardOutput = passwordStream
            }
        }

        doLast {
            ext.set("database", databaseStream.toString().replace("\"", "").trim())
            ext.set("host", hostStream.toString().replace("\"", "").trim())
            ext.set("port", portStream.toString().replace("\"", "").trim())
            ext.set("privateHost", privatehostStream.toString().replace("\"", "").trim())
            ext.set("id", idStream.toString().replace("\"", "").trim())
            ext.set("user", userStream.toString().replace("\"", "").trim())
            ext.set("password", passwordStream.toString().replace("\"", "").trim())
        }
    }

    register("terraform-droplet-ip") {
        group = "deploy"

        onlyIf {
            File("$terraformDir/terraform.tfstate").exists()
        }

        @Suppress("UnstableApiUsage")
        doNotTrackState("We should not cache secrets")

        val dropletIpStream = ByteArrayOutputStream()

        doFirst {
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "output", "droplet-ip")
                standardOutput = dropletIpStream
            }
        }

        doLast {
            ext.set("dropletIp", dropletIpStream.toString().replace("\"", "").trim())
        }
    }
    register("ansible") {
        group = "deploy"

        dependsOn("ktor-config")
        mustRunAfter("terraform-plan", "terraform-apply")

        // Only depend on create-inventory if an inventory file needs to be generated from gradle.properties
        File("$ansibleDir/inventory")
            .takeIf { it.exists() }
            ?: dependsOn("create-inventory")

        doLast {
            val sshUser = project.properties["sshUser"]
                ?.let { it as String }
                ?.takeIf { it.isNotEmpty() }
                ?: "root"

            project.exec {
                workingDir(ansibleDir)
                commandLine("ansible-playbook",
                    "-u", sshUser,
//                    "--extra-vars", "{\"priceChartingKey\": $priceChartingKey, \"\"}",
                    "-i", pathToAnsibleInventory,
                    "--flush-cache",
                    "portalsaas.yaml"
                )
            }
        }
    }

    register("create-inventory") {
        group = "deploy"

        dependsOn("terraform-droplet-ip")
        mustRunAfter("terraform-apply")

        onlyIf {
            logger.info("Checking for the presence of infrastructure/terraform/data.tf")
            !File(pathToAnsibleInventory).exists()
        }

        doLast {
            val dropletIp = ext.get("dropletIp")

            File(pathToAnsibleInventory).writeText("""
            [portalsaas]
            $dropletIp
        """.trimIndent())
        }
    }
}

tasks.register("ktor-config") {
    dependsOn("terraform-db-output")

    doLast {
        val pathToTemplate = "$pathToResources/application.conf.hbs"
        val dest = File("$pathToResources/application.conf")

        val rawTemplate: String = File(pathToTemplate)
            .takeIf { it.exists() }
            ?.readText(Charsets.UTF_8)
            ?: throw GradleException("application.conf.hbs not found!")
        val handlebars = Handlebars()
        val template = handlebars.compileInline(rawTemplate)

        // Default values are the local docker config
        val host = runCatching { ext.get("privateHost") }.getOrNull()
        val port = runCatching { ext.get("port") }.getOrNull()
        val db = runCatching { ext.get("database") }.getOrNull()
        val user = runCatching { ext.get("user").toString() }.getOrNull()
        val password = runCatching { ext.get("password").toString() }.getOrNull()

        val result = template.apply(mapOf(
            "host" to host,
            "port" to port,
            "db" to db,
            "driver" to "org.postgresql.Driver",
            "username" to user,
            "password" to password,
            "maxPool" to "10",
            "discordToken" to discordToken,
            "pricechartingToken" to pricechartingToken
        ))

        if (dest.exists() && !dest.delete()) {
            logger.warn("Could not clean up old application.conf file!")
        }

        dest.createNewFile()
        dest.writeText(result)
    }
}
