plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
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


tasks.test {
    useJUnitPlatform()
}