package com.portalsoup.saas.core.ktor.modules

import com.portalsoup.saas.api.healthcheckApi
import com.portalsoup.saas.api.helloWorldApi
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*

fun Application.api() {
    log.info("Initializing routing...")

    // Setup
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)

        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.AcceptEncoding)
        allowHeader(HttpHeaders.AcceptLanguage)
        allowHeader(HttpHeaders.AccessControlRequestHeaders)
        allowHeader(HttpHeaders.AccessControlRequestMethod)
        allowHeader(HttpHeaders.Connection)
        allowHeader(HttpHeaders.Host)
        allowHeader(HttpHeaders.Origin)
        allowHeader(HttpHeaders.Referrer)
        allowHeader(HttpHeaders.UserAgent)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.Cookie)
        allowHeader(HttpHeaders.ContentLength)
        allowHeader(HttpHeaders.ContentType)


        allowHost("localhost")
        allowHost("localhost:8080")

        allowSameOrigin = true

        allowCredentials = true
    }



    install(ContentNegotiation) {
        gson()
    }

    install(Routing) {
        // Configure client
        staticResources("/", "static", "index.html")

        healthcheckApi()
        helloWorldApi()
    }
}