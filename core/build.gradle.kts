plugins {
    kotlin("jvm") version "1.9.0" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":data"))
    implementation(project(":common"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation(kotlin("reflect"))

    implementation(platform("io.arrow-kt:arrow-stack:1.2.0"))
    // arrow versions are dictated by arrow-stack above
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")

    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-network-tls-certificates")
    implementation("io.ktor:ktor-client-content-negotiation")

    implementation("org.quartz-scheduler:quartz")

    implementation("com.zaxxer:HikariCP")
    implementation("org.flywaydb:flyway-core")

    implementation("org.postgresql:postgresql")
    implementation("org.jetbrains.exposed:exposed-core")
    implementation("org.jetbrains.exposed:exposed-dao")
    implementation("org.jetbrains.exposed:exposed-java-time")
    implementation("org.jetbrains.exposed:exposed-jdbc")

    //gpx
    implementation("io.jenetics:jpx")
    implementation("org.openstreetmap.jmapviewer:jmapviewer")

    implementation("ch.qos.logback:logback-classic")
    implementation("org.slf4j:slf4j-api")
    implementation("org.json:json")

    implementation("com.rometools:rome")

    implementation("net.dv8tion:JDA")


    // math processing
    implementation("com.notkamui.libs:keval")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


tasks.test {
    useJUnitPlatform()
}