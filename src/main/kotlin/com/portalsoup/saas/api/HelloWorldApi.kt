package com.portalsoup.saas.api

import com.portalsoup.saas.data.tables.HelloWorld
import com.portalsoup.saas.data.tables.HelloWorldTable
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Routing.helloWorldApi() {
    get("/hello") {
        val helloer: String = call.parameters["name"] ?: "Anonymous"

        val previousHelloers: List<HelloWorld> = transaction {
            val query: Query = HelloWorldTable.selectAll()

            query.map {
                HelloWorld.fromRow(it)
            }
        }

        transaction {
            HelloWorldTable.insert {
                it[name] = helloer
                it[createdOn] = LocalDate.now()
            }
        }

        val formattedHelloers = previousHelloers
            .joinToString("\n") {
                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                "${formatter.format(it.createdOn)} ${it.name}"
            }

        call.respond("Those who have Hello'd before you...\n$formattedHelloers")
    }
}