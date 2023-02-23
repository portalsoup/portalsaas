package com.portalsoup.saas.api

import com.portalsoup.saas.core.db.execAndMap
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.healthcheckApi() {
    get("/health") {
        call.respond("Alive!")
    }

    get("roll") {
        val min = call.parameters["min"] ?: 1
        val max = call.parameters["max"] ?: 6
        val result: List<String> = transaction {
            "SELECT floor(random() * ($max - $min + 1) + $min)::int as random_number;".execAndMap {
                it.getString("random_number")
            }
        }
        call.respond(result.first())
    }
}

