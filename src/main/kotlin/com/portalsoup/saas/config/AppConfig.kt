package com.portalsoup.saas.config

import org.koin.core.component.KoinComponent

data class AppConfig(
    val jdbcConfig: Jdbc
) : KoinComponent

data class Jdbc(
    val url: String,
    val driver: String,
    val username: String,
    val password: String,
    val maxPool: Int
)
