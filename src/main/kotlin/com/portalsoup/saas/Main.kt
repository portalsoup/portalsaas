package com.portalsoup.saas

import com.portalsoup.saas.core.configureRouting
import com.portalsoup.saas.core.configureSerialization
import com.portalsoup.saas.schedule.PriceChartingUpdater
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.flywaydb.core.Flyway

val dbConfig = HikariConfig().apply {
    jdbcUrl = ""
    driverClassName = ""
    username = ""
    password = ""
    maximumPoolSize = 10
}

val dataSource = HikariDataSource(dbConfig)

fun main() {
    PriceChartingUpdater().startScheduler()
    Flyway.configure().dataSource(dataSource).load().migrate()
    initKtor()
}

fun initKtor() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}