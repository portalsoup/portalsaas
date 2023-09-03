import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jknack.handlebars.Handlebars

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.github.jknack:handlebars:4.3.1")
    }
}

plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.0" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "1.9.0"
}

val pathToResources = rootDir.resolve("discord/src/main/resources/")

// Properties loaded from gradle.properties
val priceChartingKey: String by project
val doToken: String? by project
val doSshId: String? by project

val discordToken: String? by project
val myDiscordID: String by project
val myDiscordGuildID: String by project
val myDiscordGuildVIPChannelID: String by project

val pricechartingToken: String? by project
val openaiToken: String? by project
val stravaToken: String by project
val myStravaAthleteID: String by project


dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")

    //gpx
    implementation("io.jenetics:jpx")
    implementation("org.openstreetmap.jmapviewer:jmapviewer")

    implementation("ch.qos.logback:logback-classic")
    implementation("org.slf4j:slf4j-api")
    implementation("org.json:json")

    implementation("com.rometools:rome")

    // math processing
    implementation("com.notkamui.libs:keval")

    // Discord deps
    implementation("net.dv8tion:JDA")
    implementation("com.sedmelluq:lavaplayer")
    implementation("com.cjcrafter:openai")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("com.portalsoup.saas.MainKt")
}

tasks {
    named<ShadowJar>("shadowJar") {
//        inputs.dir("$rootDir/src")

        mustRunAfter(":client:package")
        dependsOn("application-config")

        archiveBaseName.set("shadow")
        archiveVersion.set("")
        archiveClassifier.set("")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.portalsoup.saas.MainKt"))
        }
    }

    test {
        useJUnitPlatform()
    }
    register("deploy") {
        mustRunAfter(shadowJar)
    }

    build {
        mustRunAfter(shadowJar)
    }

    clean {
        dependsOn("deleteStaticAssets")
        delete(projectDir.resolve("src/main/resources/application.json"))
    }

    create<Delete>("deleteStaticAssets") {
        delete(projectDir.resolve("src/main/resources/static"))
    }


    register("application-config") {
        doLast {
            val pathToTemplate = "$pathToResources/application.json.hbs"
            val pathToDest = "$pathToResources/application.json"

            val templateFile = File(pathToTemplate)
            val destFile = File(pathToDest)

            val rawTemplate: String = templateFile
                .takeIf { it.exists() }
                ?.readText(Charsets.UTF_8)
                ?: throw GradleException("application.json.hbs not found at the path $pathToTemplate!")
            val handlebars = Handlebars()
            val template = handlebars.compileInline(rawTemplate)

            // Default values are the local docker config
            val host = runCatching { ext.get("privateHost") }.getOrNull()
            val port = runCatching { ext.get("port") }.getOrNull()
            val db = runCatching { ext.get("database") }.getOrNull()
            val user = runCatching { ext.get("user").toString() }.getOrNull()
            val password = runCatching { ext.get("password").toString() }.getOrNull()

            val result = template.apply(
                mapOf(
                    "host" to host,
                    "port" to port,
                    "db" to db,
                    "driver" to "org.postgresql.Driver",
                    "username" to user,
                    "password" to password,
                    "maxPool" to "10",
                    "discordToken" to discordToken,
                    "myDiscordID" to myDiscordID,
                    "myDiscordGuildID" to myDiscordGuildID,
                    "myDiscordGuildVIPChannelID" to myDiscordGuildVIPChannelID,
                    "pricechartingToken" to pricechartingToken,
                    "openaiToken" to openaiToken,
                    "stravaToken" to stravaToken,
                    "stravaAthleteID" to myStravaAthleteID
                )
            )

            if (destFile.exists() && !destFile.delete()) {
                logger.warn("Could not clean up old application.conf file!")
            }

            destFile.createNewFile()
            destFile.writeText(result)
        }
    }
}