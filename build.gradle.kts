import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

object Versions {
    const val jpx = "1.4.0"
    const val exposed = "0.36.2"
    const val hikari = "2.7.8"
    const val flyway = "9.8.2"
    const val ktor = "2.1.3"
    const val psql = "42.5.0"
    const val h2 = "1.4.200"
    const val kotlinReflect = "1.2.51"
    const val slf4j = "1.7.30"
    const val logback = "1.2.3"

    // testing
    const val junit = "4.12"
    const val testng = "7.3.0"
    const val hamkrest = "1.7.0.3"
}

plugins {
    kotlin("jvm") version "1.7.21"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("plugin.serialization") version "1.7.21"
    id("org.flywaydb.flyway") version "9.8.1"
}

group = "com.portalsoup"
version = "1.0-SNAPSHOT"
val ktorVersion = "2.1.3"

// Variables required from gradle.properties
val mainClassName: String by project
val priceChartingKey: String by project
val doToken: String by project
val deploySshId: String by project
val ansibleDeployIP: String by project

// Shortcuts
val ansibleDir = "$rootDir/infrastructure/ansible/"
val terraformDir = "$rootDir/infrastructure/terraform/"
val pathToAnsibleInventory = "$ansibleDir/inventory"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty-jvm:${Versions.ktor}")
    implementation("io.ktor:ktor-client-core:${Versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${Versions.ktor}")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")

    implementation("io.ktor:ktor-server-content-negotiation:${Versions.ktor}")

    implementation("org.quartz-scheduler:quartz:2.3.2")

    implementation("io.jenetics:jpx:${Versions.jpx}")
    implementation("com.zaxxer:HikariCP:${Versions.hikari}")
    implementation("org.flywaydb:flyway-core:${Versions.flyway}")

    implementation("org.postgresql:postgresql:${Versions.psql}")
    implementation("org.jetbrains.exposed:exposed-core:${Versions.exposed}")
    implementation("org.jetbrains.exposed:exposed-dao:${Versions.exposed}")
    implementation("org.jetbrains.exposed:exposed-java-time:${Versions.exposed}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${Versions.exposed}")

    implementation("io.insert-koin:koin-core:3.3.2")

    implementation("io.jenetics:jpx:${Versions.jpx}")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set(project.property("mainClassName") as String)
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        archiveVersion.set("")
        archiveClassifier.set("")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to project.property("mainClassName") as String))
        }
    }
    register<Exec>("dockerDown") {
        commandLine("docker-compose", "down")
    }

    register<Exec>("dockerBuild") {
        dependsOn("dockerDown")
        commandLine("docker-compose", "build")
    }

    register<Exec>("dockerRun") {
        dependsOn("build", "dockerDown", "dockerBuild")
        commandLine("docker-compose", "up", "-d", "app")
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
    }
}

// Infrastructure
tasks {
    create("deploy") {
        dependsOn("shadowJar", "terraform-apply", "ansible")
        group = "deploy"
    }

    create("terraform-init") {
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
    create("terraform-plan") {
        group = "deploy"

        onlyIf {
            println("Checking for the presence of infrastructure/terraform/data.tf")
            File("$terraformDir/data.tf").exists()
        }

        // configure stdin for prompts
        val run by getting(JavaExec::class) {
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

    create("terraform-apply") {
        group = "deploy"

        dependsOn("terraform-plan")

        onlyIf {
            println("Checking for the presence of infrastructure/terraform/data.tf")
            File("$terraformDir/data.tf").exists()
        }

        doLast {
            project.exec {
                workingDir("$terraformDir")
                commandLine("terraform", "apply",
                    "-auto-approve",
                    "-var", "ssh_id=${deploySshId}",
                    "-var", "do_token=$doToken"
                )
            }
        }
    }

    create("ansible") {
        group = "deploy"

        mustRunAfter("terraform-plan", "terraform-apply", "shadowJar")

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
                    "--extra-vars", "{\"priceChartingKey\": $priceChartingKey, \"\"}",
                    "-i", pathToAnsibleInventory,
                    "--flush-cache",
                    "portalsaas.yml"
                )
            }
        }
    }

    create("create-inventory") {
        group = "deploy"

        onlyIf {
            println("Checking for the presence of infrastructure/terraform/data.tf")
            !File(pathToAnsibleInventory).exists()
        }

        doLast {
            File(pathToAnsibleInventory).writeText("""
            [portalsaas]
            $ansibleDeployIP
        """.trimIndent())
        }
    }
}