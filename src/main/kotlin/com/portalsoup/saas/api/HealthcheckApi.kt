package com.portalsoup.saas.api

import com.portalsoup.saas.TestInjection
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

private val x: TestInjection by inject(TestInjection::class.java)

fun Routing.healthcheckApi() {
    get("/health") {
        call.respond("Alive!")
    }

    get("/test") {
        call.respond("test")
    }

    get {
        call.respond(x.str)
    }
}
