package com.portalsoup.saas.ktor.modules

import com.portalsoup.saas.api.gpxApi
import com.portalsoup.saas.api.healthcheckApi
import com.portalsoup.saas.api.helloWorldApi
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.api() {
    log.info("Initializing routing...")


    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(Routing) {
        // Configure client
        staticResources("/", "static", "index.html")

        route("/api") {
            this@install.healthcheckApi()
            this@install.helloWorldApi()
            this@install.gpxApi()
        }
    }
}