plugins {
    kotlin("jvm") version "1.9.0" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation("com.zaxxer:HikariCP")
    implementation("org.flywaydb:flyway-core")

    implementation("org.postgresql:postgresql")
    implementation("org.jetbrains.exposed:exposed-core")
    implementation("org.jetbrains.exposed:exposed-dao")
    implementation("org.jetbrains.exposed:exposed-java-time")
    implementation("org.jetbrains.exposed:exposed-jdbc")

    implementation("ch.qos.logback:logback-classic")
    implementation("org.slf4j:slf4j-api")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


tasks.test {
    useJUnitPlatform()
}