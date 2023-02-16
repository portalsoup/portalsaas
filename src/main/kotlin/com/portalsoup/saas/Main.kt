package com.portalsoup.saas

import com.portalsoup.saas.api.healthcheckApi
import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.DatabaseFactory
import com.portalsoup.saas.core.Retrier
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.coreModule() {
    log.info("Initializing core module...")
    val appConfig = AppConfig.default(environment)

    DatabaseFactory().init(appConfig)


    val appModule = module {
        single { appConfig }
        single { TestInjection("This value was dependency injected!") }
    }

    startKoin {
        modules(
            appModule
        )
    }


    routing {
        healthcheckApi()
    }
}

data class TestInjection(val str: String)