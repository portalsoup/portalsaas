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

repositories {
    mavenCentral()
    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }
    maven {
        url = uri("https://josm.openstreetmap.de/nexus/content/groups/public")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.1")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.1")
    implementation("io.ktor:ktor-client-core:2.3.1")
    implementation("io.ktor:ktor-client-cio:2.3.1")
    implementation("io.ktor:ktor-network-tls-certificates:2.3.1")
    implementation("io.ktor:ktor-server-cors:2.3.1")
    implementation("io.ktor:ktor-auth-jwt:1.6.8")
    implementation("io.ktor:ktor-server-sessions:2.3.1")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.1")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")

    implementation("org.quartz-scheduler:quartz:2.3.2")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.flywaydb:flyway-core:9.17.0")

    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    implementation("io.insert-koin:koin-core:3.4.0")


    //gpx
    implementation("io.jenetics:jpx:3.0.1")
    implementation("org.openstreetmap.jmapviewer:jmapviewer:2.0")

    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.json:json:20220924")

    implementation("com.rometools:rome:2.1.0")

    // math processing
    implementation("com.notkamui.libs:keval:0.9.0")

    // Discord deps
// https://mvnrepository.com/artifact/net.dv8tion/JDA
    implementation("net.dv8tion:JDA:5.0.0-beta.10")
    implementation("com.sedmelluq:lavaplayer:1.3.77")
    implementation("com.cjcrafter:openai:1.3.0")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0-M1")
}

application {
    mainClass.set("com.portalsoup.saas.MainKt")
}

tasks {
    named<ShadowJar>("shadowJar") {
//        inputs.dir("$rootDir/src")

        mustRunAfter("client:package")
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

    clean {
        dependsOn("deleteStaticAssets")
        delete(projectDir.resolve("src/main/resources/application.conf"))
    }

    build {
        mustRunAfter(shadowJar)
    }

    create<Delete>("deleteStaticAssets") {
        delete(projectDir.resolve("src/main/resources/static"))
    }
}
