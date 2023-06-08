package com.portalsoup.saas.core.ktor.modules

import com.portalsoup.saas.api.healthcheckApi
import com.portalsoup.saas.api.helloWorldApi
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun Application.api() {
    log.info("Initializing routing...")


    install(ContentNegotiation) {
        gson()
    }

    install(Routing) {
        // Configure client
        staticResources("/", "static", "index.html")

        route("/api") {
            this@install.healthcheckApi()
            this@install.helloWorldApi()
        }
    }
}