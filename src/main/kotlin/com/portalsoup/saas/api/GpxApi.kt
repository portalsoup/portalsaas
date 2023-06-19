package com.portalsoup.saas.api

import com.portalsoup.saas.service.StravaManager
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.gpxApi() {

    get("gpx/import") {
        val result = StravaManager().listRoutesAPI()
        call.respond(result)
    }

}
