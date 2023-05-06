package com.portalsoup.saas.core.ktor.modules

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.db.DatabaseFactory
import com.portalsoup.saas.core.koin.KoinMain
import io.ktor.server.application.*


@Suppress("unused") // This module is linked from config, it's not actually unused
fun Application.root() {
    log.info("Initializing core module...")
    val appConfig = AppConfig.default(environment)

    DatabaseFactory().init(appConfig)
    log.info("Database ready to go")

    KoinMain.init(appConfig)

    // Initialize other modules here
    api()
    discord(appConfig) // keep this one last, it takes a bit
}
