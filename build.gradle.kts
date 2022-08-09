
plugins {
    kotlin("jvm") version "1.7.10"
    id("portalsoup-deploy") version "0.0.7"
}

group = "com.portalsoup"
version = "1.0-SNAPSHOT"

// Variables required from gradle.properties
val discordBotName: String by project
val discordBotToken: String by project
val nookipediaToken: String by project
val ansibleDeployIP: String by project
val deploySshId: String by project
val botGithubUrl: String by project
val doToken: String by project
val botPrefix: String by project

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

ansible {
    ansibleDir.set("ansible")
    ansibleHost.set("localhost")
    ansibleExtraArgsJson.set("")

    inventory {
        host("integrationtest") {
            add("localhost")
        }
    }

    playbook {
        hosts = "integrationtest"
        become = "yes"

        tasks {
            install("Install apt packages") {
                update_cache = true
                packages = mutableListOf("default-jre")
            }
        }
    }
}

terraform {
    dropletName.set("server")
    dropletResourceName.set("portalsaas")
    projectName.set("PortalSaaS")
    projectResourceName.set("portalsaas")
    digitalOceanSshId.set(digitalOceanSshId)
    digitalOceanToken.set(doToken)

    terraformDir.set(buildDir.resolve("terraform").absolutePath)
}