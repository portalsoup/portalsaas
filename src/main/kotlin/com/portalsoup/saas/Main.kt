package com.portalsoup.saas

import com.portalsoup.saas.api.healthcheckApi
import com.portalsoup.saas.api.helloWorldApi
import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.db.DatabaseFactory
import com.portalsoup.saas.discord.DiscordBot
import com.portalsoup.saas.discord.LavaPlayerAudioProvider
import com.portalsoup.saas.discord.TrackScheduler
import com.portalsoup.saas.manager.RssManager
import com.portalsoup.saas.quartz.QuartzModule
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
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

    val discordClient: GatewayDiscordClient = DiscordClient
        .create(appConfig.discordToken ?: throw RuntimeException("The discord bot should not have been initialized"))
        .gateway()
        .setEnabledIntents(
            IntentSet.of(
            Intent.DIRECT_MESSAGES,
            Intent.GUILD_MESSAGES,
            Intent.GUILDS,
            Intent.GUILD_VOICE_STATES
        ))
        .login()
        .block()
        ?: throw RuntimeException("Failed to connect to Discord")


    // Koin dependency management
    val appModule = module {
        single { appConfig }
        single { playerManager }
        single { RssManager }
        single { LavaPlayerAudioProvider(player) }
        single { TrackScheduler(player) }
        single { HttpClient(CIO) {
        }}
        single { scheduler }
        single { discordClient }
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

    if (appConfig.discordToken.isNotEmpty()) {
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
