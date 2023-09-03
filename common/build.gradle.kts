plugins {
    kotlin("jvm") version "1.9.0" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation(kotlin("reflect"))

    implementation("org.json:json")
    implementation("ch.qos.logback:logback-classic")
    implementation("org.slf4j:slf4j-api")


    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}