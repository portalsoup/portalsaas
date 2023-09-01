import Build_gradle.ConstraintType.*

group = "com.portalsoup"
version = "1.0-SNAPSHOT"
val ktorVersion = "2.1.3"

enum class ConstraintType { PREFER, STRICT, REJECT, REQUIRE }
data class DepConstraint(val scope: String, val notation: String, val type: ConstraintType, val version: String)
object Versions {
    val ktor = "2.3.1"
    val exposed = "0.41.1"
}

val dependenciesList = listOf(
    DepConstraint("implementation", "io.ktor:ktor-server-core", PREFER, Versions.ktor),
    DepConstraint("implementation", "io.ktor:ktor-server-netty-jvm", PREFER, Versions.ktor),
    DepConstraint("implementation", "io.ktor:ktor-client-core", PREFER, Versions.ktor),
    DepConstraint("implementation", "io.ktor:ktor-client-cio", PREFER, Versions.ktor),
    DepConstraint("implementation", "io.ktor:ktor-network-tls-certificates", PREFER, Versions.ktor),
    DepConstraint("implementation", "io.ktor:ktor-server-cors", PREFER, Versions.ktor),
    DepConstraint("implementation", "io.ktor:ktor-auth-jwt", PREFER, "1.6.8"),
    DepConstraint("implementation", "io.ktor:ktor-server-sessions", PREFER, Versions.ktor),
    DepConstraint("implementation", "io.ktor:ktor-server-content-negotiation", PREFER, Versions.ktor),
    DepConstraint("implementation", "io.ktor:ktor-client-content-negotiation", PREFER, Versions.ktor),
    DepConstraint("implementation", "io.ktor:ktor-serialization-kotlinx-json", PREFER, Versions.ktor),

    DepConstraint("implementation", "org.quartz-scheduler:quartz", PREFER, "2.3.2"),

    DepConstraint("implementation", "com.zaxxer:HikariCP", PREFER, "5.0.1"),
    DepConstraint("implementation", "org.flywaydb:flyway-core", PREFER, "9.17.0"),

    DepConstraint("implementation", "org.postgresql:postgresql", PREFER, "42.6.0"),
    DepConstraint("implementation", "org.jetbrains.exposed:exposed-core", PREFER, Versions.exposed),
    DepConstraint("implementation", "org.jetbrains.exposed:exposed-dao", PREFER, Versions.exposed),
    DepConstraint("implementation", "org.jetbrains.exposed:exposed-java-time", PREFER, Versions.exposed),
    DepConstraint("implementation", "org.jetbrains.exposed:exposed-jdbc", PREFER, Versions.exposed),

    DepConstraint("implementation", "io.insert-koin:koin-core", PREFER, "3.4.0"),

    DepConstraint("implementation", "io.jenetics:jpx", PREFER, "3.0.1"),
    DepConstraint("implementation", "org.openstreetmap.jmapviewer:jmapviewer", PREFER, "2.0"),

    DepConstraint("implementation", "ch.qos.logback:logback-classic", PREFER, "1.4.5"),
    DepConstraint("implementation", "org.slf4j:slf4j-api", PREFER, "2.0.5"),
    DepConstraint("implementation", "org.json:json", PREFER, "20220924"),

    DepConstraint("implementation", "com.rometools:rome", PREFER, "2.1.0"),

    DepConstraint("implementation", "com.notkamui.libs:keval", PREFER, "0.9.0"),

    DepConstraint("implementation", "net.dv8tion:JDA", PREFER, "5.0.0-beta.10"),
    DepConstraint("implementation", "com.sedmelluq:lavaplayer", PREFER, "1.3.77"),
    DepConstraint("implementation", "com.cjcrafter:openai", PREFER, "1.3.0"),

    DepConstraint("implementation", "com.github.jknack:handlebars", PREFER, "4.3.1"),

    DepConstraint("testImplementation", "org.junit.jupiter:junit-jupiter", PREFER, "5.10.0-M1")
)

// Apply versioning constraints to all java subprojects
allprojects {
    plugins.withType(JavaPlugin::class.java).whenPluginAdded {
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
            constraints {
                dependenciesList.onEach {
                    add(it.scope, it.notation) {
                        version {
                            when (it.type) {
                                PREFER -> prefer(it.version)
                                STRICT -> strictly(it.version)
                                REJECT -> reject(it.version)
                                REQUIRE -> require(it.version)
                            }
                        }
                    }
                }
            }
        }
    }
}