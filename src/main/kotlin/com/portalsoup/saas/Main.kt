package com.portalsoup.saas

import com.portalsoup.saas.api.healthcheckApi
import com.portalsoup.saas.api.helloWorldApi
import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.DatabaseFactory
import com.portalsoup.saas.discord.DMusic
import com.portalsoup.saas.discord.command.LavaPlayerAudioProvider
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import discord4j.voice.AudioProvider
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main(args: Array<String>) {

    EngineMain.main(args)
}

fun Application.coreModule() {
    log.info("Initializing core module...")
    val appConfig = AppConfig.default(environment)

    DatabaseFactory().init(appConfig)
    log.info("Database ready to go")

    log.info("Initializing audio player...")
    val playerManager = DefaultAudioPlayerManager()

    playerManager.configuration
        .setFrameBufferFactory(::NonAllocatingAudioFrameBuffer)

    AudioSourceManagers.registerRemoteSources(playerManager)

    val player = playerManager.createPlayer()
    val provider: AudioProvider = LavaPlayerAudioProvider(player)

    // Koin dependency management
    val appModule = module {
        single { appConfig }
        single { TestInjection("This value was dependency injected!") }
        single { playerManager }
        single { LavaPlayerAudioProvider(player) }
    }

    startKoin {
        modules(
            appModule
        )
    }

    log.info("Initializing routing...")
    routing {
        healthcheckApi()
        helloWorldApi()
    }
    log.info("Initializing discord bot...")
    DMusic().init()

    log.info("Discord bot ready to go")
}

data class TestInjection(val str: String)