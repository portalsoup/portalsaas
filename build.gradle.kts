import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.21"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("plugin.serialization") version "1.7.21"
}

group = "com.portalsoup"
version = "1.0-SNAPSHOT"
val ktorVersion = "2.1.3"

project.setProperty("mainClassName", "com.portalsoup.saas.MainKt")
// Variables required from gradle.properties
val mainClassName: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
    implementation("io.ktor:ktor-client-core:${Versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${Versions.ktor}")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")

    implementation("io.ktor:ktor-server-content-negotiation:${Versions.ktor}")

    implementation("org.quartz-scheduler:quartz:2.3.2")

    implementation(Dependencies.jpx)
    implementation(Dependencies.exposedCore)
    implementation(Dependencies.hikari)
    implementation(Dependencies.flywayCore)

    implementation(Dependencies.psql)
    implementation(Dependencies.exposedCore)
    implementation(Dependencies.exposedDao)
    implementation(Dependencies.exposedJavaTime)
    implementation(Dependencies.exposedJdbc)

    implementation(Dependencies.jpx)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
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
        dependsOn("shadowJar", "dockerDown", "dockerBuild")
        commandLine("docker-compose", "up", "-d", "app")
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

object Versions {
    const val jpx = "1.4.0"
    const val exposed = "0.36.2"
    const val hikari = "2.7.8"
    const val flyway = "6.5.2"
    const val ktor = "2.1.3"
    const val psql = "42.2.14"
    const val h2 = "1.4.200"
    const val kotlinReflect = "1.2.51"
    const val slf4j = "1.7.30"
    const val logback = "1.2.3"

    // testing
    const val junit = "4.12"
    const val testng = "7.3.0"
    const val hamkrest = "1.7.0.3"
}

object Dependencies {

    object Subprojects {
        const val common = ":common"
        const val core = ":core"
        const val data = ":data"
    }

    val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinReflect}"

    val jpx = "io.jenetics:jpx:${Versions.jpx}"
    val hikari = "com.zaxxer:HikariCP:${Versions.hikari}"
    val flywayCore = "org.flywaydb:flyway-core:${Versions.flyway}"

    val ktorNetty = "io.ktor:ktor-server-netty:${Versions.ktor}"
    val ktorGson = "io.ktor:ktor-gson:${Versions.ktor}"
    val ktorSeverSessions = "io.ktor:ktor-server-sessions:${Versions.ktor}"
    val ktorAuthJwt = "io.ktor:ktor-auth-jwt:${Versions.ktor}"

    val exposedCore = "org.jetbrains.exposed:exposed-core:${Versions.exposed}"
    val exposedDao = "org.jetbrains.exposed:exposed-dao:${Versions.exposed}"
    val exposedJdbc = "org.jetbrains.exposed:exposed-jdbc:${Versions.exposed}"
    val exposedJavaTime = "org.jetbrains.exposed:exposed-java-time:${Versions.exposed}"

    val psql = "org.postgresql:postgresql:${Versions.psql}"
//    val h2 = "com.h2database:h2:${Versions.h2}"

    val slf4j = "org.slf4j:slf4j-api:${Versions.slf4j}"
    val logbackCore = "ch.qos.logback:logback-classic:${Versions.logback}"
    val logbackClassic = "ch.qos.logback:logback-core:${Versions.logback}"

    // testing
    val junit = "junit:junit:${Versions.junit}"
    val testng = "org.testng:testng:${Versions.testng}"
    val hamkrest = "com.natpryce:hamkrest:${Versions.hamkrest}"

}