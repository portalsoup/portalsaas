package com.portalsoup.saas.api

import com.portalsoup.saas.TestInjection
import com.portalsoup.saas.core.db.execAndMap
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.java.KoinJavaComponent.inject
import java.sql.ResultSet

private val x: TestInjection by inject(TestInjection::class.java)

fun Routing.healthcheckApi() {
    get("/health") {
        call.respond("Alive!")
    }

    get("/test") {
        call.respond("test")
    }

    get("roll") {
        val min = call.parameters["min"] ?: 1
        val max = call.parameters["max"] ?: 6
        val result: List<String> = transaction {
            "SELECT floor(random() * ($max - $min + 1) + $min)::int as random_number;".execAndMap {
                it.getString("random_number")
            }
        }
        println(result)
        call.respond(result.first())
    }
}

