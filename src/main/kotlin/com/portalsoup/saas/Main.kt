package com.portalsoup.saas

import com.portalsoup.saas.api.healthcheckApi
import com.portalsoup.saas.api.helloWorldApi
import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.db.DatabaseFactory
import com.portalsoup.saas.discord.DiscordBot
import com.portalsoup.saas.discord.LavaPlayerAudioProvider
import com.portalsoup.saas.discord.TrackScheduler
import com.portalsoup.saas.quartz.QuartzModule
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.quartz.impl.StdSchedulerFactory

fun main(args: Array<String>) {
    scheduler.start()
    EngineMain.main(args)
    scheduler.shutdown()
}

val scheduler = StdSchedulerFactory.getDefaultScheduler() ?: throw RuntimeException("Failed to initialize quartz factory")

@Suppress("unused") // This module is linked from config, it's not actually unused
fun Application.coreModule() {
    log.info("Initializing core module...")
    val appConfig = AppConfig.default(environment)

    DatabaseFactory().init(appConfig)
    log.info("Database ready to go")

    log.info("Initializing audio player...")
    val playerManager = initLavaPlayer()
    val player = playerManager.createPlayer()


    // Koin dependency management
    val appModule = module {
        single { appConfig }
        single { playerManager }
        single { LavaPlayerAudioProvider(player) }
        single { TrackScheduler(player) }
        single { HttpClient(CIO) {

        }}
        single { scheduler }
    }

    startKoin {
        modules(
            appModule
        )
    }

    // collect quartz jobs
    QuartzModule()

    log.info("Initializing routing...")
    routing {
        healthcheckApi()
        helloWorldApi()
    }

    if (! appConfig.discordToken.isNullOrEmpty()) {
        log.info("Initializing discord bot...")
        DiscordBot().init()
        log.info("Discord bot ready to go")

    }

}

fun initLavaPlayer(): DefaultAudioPlayerManager {
    val playerManager = DefaultAudioPlayerManager()

    playerManager.configuration
        .setFrameBufferFactory(::NonAllocatingAudioFrameBuffer)

    AudioSourceManagers.registerRemoteSources(playerManager)
    return playerManager
}
