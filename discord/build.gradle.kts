import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.21"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("plugin.serialization") version "1.7.21"
}

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

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
