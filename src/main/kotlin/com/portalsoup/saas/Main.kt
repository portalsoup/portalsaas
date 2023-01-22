package com.portalsoup.saas

import com.portalsoup.saas.api.healthcheckApi
import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.config.Jdbc
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.coreModule() {
    fun createAppConfig(): AppConfig =
        AppConfig(
            Jdbc(
                url = environment.config.property("jdbc.url").getString(),
                driver = environment.config.property("jdbc.driver").getString(),
                username = environment.config.property("jdbc.username").getString(),
                password = environment.config.property("jdbc.password").getString(),
                maxPool = environment.config.property("jdbc.maxPool").getString().toInt()

            )
        )

    val appModule = module {
        singleOf(::createAppConfig)
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