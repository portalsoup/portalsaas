package com.portalsoup.saas.ktor.modules

import com.portalsoup.saas.db.DatabaseFactory
import io.ktor.server.application.*


@Suppress("unused") // This module is linked from config, it's not actually unused
fun Application.root() {
    log.info("Initializing core module...")

    DatabaseFactory().init()
    log.info("Database ready to go")


    // Initialize other modules here
    api()
}
