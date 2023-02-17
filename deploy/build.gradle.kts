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
val doToken: String by project
val deploySshId: String by project
val ansibleDeployIP: String by project
val discordToken: String by project

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
            println("Checking for the presence of infrastructure/terraform/data.tf")
            File("$terraformDir/data.tf").exists()
        }

        doLast {
            project.exec {
                workingDir("$terraformDir")
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
            println("Checking for the presence of infrastructure/terraform/data.tf")
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
                    "-var", "ssh_id=${deploySshId}",
                    "-var", "do_token=$doToken"
                )
            }
            when (planResult.exitValue) {
                0 -> {
                    println("Accept this plan? (Type 'yes' to accept)")
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
            println("Checking for the presence of infrastructure/terraform/data.tf")
            File("$terraformDir/data.tf").exists()
        }

        doLast {
            project.exec {
                workingDir(terraformDir)
                commandLine("terraform", "apply",
                    "-auto-approve",
                    "-var", "ssh_id=${deploySshId}",
                    "-var", "do_token=$doToken"
                )
            }
        }
    }

    register("terraform-db-output") {
        group = "deploy"

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
            println("Checking for the presence of infrastructure/terraform/data.tf")
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

// ktor config generation
//tasks.register("ktor-config") {
//    group = "build"
//
//    dependsOn("terraform-db-output")
//
//    rootProject.tasks.getByName("build") {
//        mustRunAfter("ktor-config")
//    }
//
//    doLast {
//        val static = File("$pathToResources/application.static.conf")
//        val dest = File("$pathToResources/application.conf")
//
//        if (dest.exists() && !dest.delete()) {
//            logger.warn("Could not clean up old application.conf file!")
//        }
//        static.copyTo(dest, true)
//
//        File("$terraformDir/terraform.tfstate")
//            .takeIf { it.exists() }
//            ?.let {
//
//                val host = ext.get("privateHost")
//                val port = ext.get("port")
//                val db = ext.get("database")
//                val username = ext.get("user")
//                val password = ext.get("password")
//
//                println("The found host was: $host")
//
//                // Merge the static and dynamic portions
//                dest.appendText("\n\n")
//                dest.appendText("""
//        jdbc {
//            url = "jdbc:postgresql://$host:$port/$db?sslmode=require"
//            driver = "org.postgresql.Driver"
//            username = "$username"
//            password = "$password"
//            maxPool = "10"
//        }
//
//        discord {
//            token = "$discordToken"
//        }
//        """.trimIndent())
//            }
//    }
//}

tasks.register("ktor-config") {
    dependsOn("terraform-db-output")

    doLast {
        val pathToTemplate = "$pathToResources/application.conf.hbs"
        val dest = File("$pathToResources/application.conf")

        val rawTemplate: String = File(pathToTemplate)
                .takeIf { it.exists() }
                ?.also { println("Found it") }
                ?.readText()
            ?: throw GradleException("application.conf.hbs not found!")
        val handlebars = Handlebars()
        val template = handlebars.compileInline(rawTemplate)

        val jdbcProps = ApplicationConfTemplate(
            url = ext.get("privateHost").toString(),
            driver = "org.postgresql.Driver",
            username = ext.get("user").toString(),
            password = ext.get("password").toString(),
            maxPool = "10",
            discordToken = discordToken
        )

        val result = template.apply(jdbcProps)

        if (dest.exists() && !dest.delete()) {
            logger.warn("Could not clean up old application.conf file!")
        }

        dest.createNewFile()
        dest.writeText(result)
    }
}

data class ApplicationConfTemplate(
    val url: String,
    val driver: String,
    val username: String,
    val password: String,
    val maxPool: String,
    val discordToken: String
)