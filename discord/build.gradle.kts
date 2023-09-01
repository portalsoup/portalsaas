import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.21"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("plugin.serialization") version "1.7.21"
}
val kotlinVersion: String by extra("1.8.21")
val kotlinxVersion: String by extra("1.7.3")
val ktorVersion: String by extra("2.3.3")
val mockkVersion: String by extra("1.13.7")

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")


    implementation(project(":core"))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-network-tls-certificates")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-auth-jwt")
    implementation("io.ktor:ktor-server-sessions")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    implementation("org.quartz-scheduler:quartz")

    implementation("com.zaxxer:HikariCP")
    implementation("org.flywaydb:flyway-core")

    implementation("org.postgresql:postgresql")
    implementation("org.jetbrains.exposed:exposed-core")
    implementation("org.jetbrains.exposed:exposed-dao")
    implementation("org.jetbrains.exposed:exposed-java-time")
    implementation("org.jetbrains.exposed:exposed-jdbc")

    implementation("io.insert-koin:koin-core")


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
        delete(projectDir.resolve("src/main/resources/application.conf"))
    }

    create<Delete>("deleteStaticAssets") {
        delete(projectDir.resolve("src/main/resources/static"))
    }
}
