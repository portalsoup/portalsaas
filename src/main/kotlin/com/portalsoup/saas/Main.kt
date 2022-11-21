package com.portalsoup.saas

import com.portalsoup.saas.core.Retrier
import com.portalsoup.saas.core.configureRouting
import com.portalsoup.saas.core.configureSerialization
import com.portalsoup.saas.schedule.PriceChartingUpdater
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.flywaydb.core.Flyway

fun main() {
    println("Entered main")

    val dbConfig = HikariConfig().apply {
        jdbcUrl = System.getenv("JDBC_URL")
        driverClassName = System.getenv("JDBC_DRIVER")
        username = System.getenv("JDBC_USERNAME")
        password = System.getenv("JDBC_PASSWORD")
        maximumPoolSize = System.getenv("JDBC_MAX_POOL").toInt()
        connectionTestQuery = "SELECT 1"
    }

    println("validating dbconfig")
    dbConfig.validate()

    println("about to get data source")
    val dataSource: HikariDataSource = Retrier("initialize-hikari") {
        println("trying to get data source...")
        HikariDataSource(dbConfig)
    }
    println("got data source")

    PriceChartingUpdater().startScheduler()
    println("about to start migration")
    Retrier("flyway-migration") {
        println("trying to migrate db...")
        Flyway.configure().dataSource(dataSource).load().migrate()
    }
    println("got past retries")
    initKtor()
}

fun initKtor() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
