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
}

group = "com.portalsoup"
version = "1.0-SNAPSHOT"
val ktorVersion = "2.1.3"

repositories {
    mavenCentral()
    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty-jvm:${Versions.ktor}")
    implementation("io.ktor:ktor-client-core:${Versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${Versions.ktor}")
    implementation("io.ktor:ktor-network-tls-certificates:${Versions.ktor}")

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
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.json:json:20220924")

    // math processing
    implementation("com.notkamui.libs:keval:0.9.0")

    // Discord deps
    implementation("com.discord4j:discord4j-core:3.2.3")
    implementation("com.sedmelluq:lavaplayer:1.3.77")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.portalsoup.saas.MainKt")
}

tasks {
    named<ShadowJar>("shadowJar") {

        archiveBaseName.set("shadow")
        archiveVersion.set("")
        archiveClassifier.set("")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.portalsoup.saas.MainKt"))
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }

    register("deploy") {
        dependsOn(shadowJar)
    }
}