package com.portalsoup.saas.config

import io.ktor.server.application.*
import org.koin.core.component.KoinComponent

data class AppConfig(
    val jdbcConfig: Jdbc
) : KoinComponent {
    companion object {
        fun default(environment: ApplicationEnvironment): AppConfig {
            val jdbcUrl = System.getenv("JDBC_URL") ?: environment.config.property("jdbc.url").getString()
            val jdbcDriver = System.getenv("JDBC_DRIVER") ?: environment.config.property("jdbc.driver").getString()
            val jdbcUsername = System.getenv("JDBC_USERNAME") ?: environment.config.property("jdbc.username").getString()
            val jdbcPassword = System.getenv("JDBC_PASSWORD") ?: environment.config.property("jdbc.password").getString()
            val jdbcMaxPool = System.getenv("JDBC_MAX_POOL").toIntOrNull() ?: environment.config.property("jdbc.maxPool").getString().toInt()

            return AppConfig(
                Jdbc(
                    url = jdbcUrl,
                    driver = jdbcDriver,
                    username = jdbcUsername,
                    password = jdbcPassword,
                    maxPool = jdbcMaxPool

                )
            )
        }
    }
}

data class Jdbc(
    val url: String,
    val driver: String,
    val username: String,
    val password: String,
    val maxPool: Int
)
