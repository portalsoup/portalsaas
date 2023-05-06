package com.portalsoup.saas.core.koin

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.discord.DiscordClientBuilder
import com.portalsoup.saas.discord.LavaPlayerAudioProvider
import com.portalsoup.saas.discord.TrackScheduler
import com.portalsoup.saas.manager.*
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import discord4j.core.GatewayDiscordClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.koin.core.module.Module
import org.koin.dsl.module
import org.quartz.impl.StdSchedulerFactory

sealed interface KoinModules {
    fun shouldInitialize(): Boolean
    fun initialize(): Module

    data class AppConfigModule(val appConfig: AppConfig): KoinModules {
        override fun shouldInitialize(): Boolean = true

        override fun initialize(): Module = module { single { appConfig } }

    }

    data class KtorClientModule(val appConfig: AppConfig): KoinModules {
        override fun shouldInitialize(): Boolean = true

        override fun initialize(): Module = module {
            single { HttpClient(CIO) }
        }
    }

    data class QuartzModule(val appConfig: AppConfig): KoinModules {
        override fun shouldInitialize(): Boolean = true

        override fun initialize(): Module = module {
            single {
                StdSchedulerFactory.getDefaultScheduler()
                    ?: throw RuntimeException("Failed to initialize quartz factory")
            }
        }
    }

    data class DiscordModule(val appConfig: AppConfig): KoinModules {
        override fun shouldInitialize() = appConfig.discordToken.isNullOrEmpty().not()
        override fun initialize(): Module {
            val client: GatewayDiscordClient = DiscordClientBuilder.build(appConfig)
            return module {
                single<GatewayDiscordClient> { client }
            }
        }
    }

    data class LavaPlayerModule(val appConfig: AppConfig): KoinModules {
        override fun shouldInitialize(): Boolean = true

        override fun initialize(): Module  {
            val playerManager = initLavaPlayer()
            val player = playerManager.createPlayer()

            return module {
                single { LavaPlayerAudioProvider(player) }
                single { TrackScheduler(player) }
            }
        }

        private fun initLavaPlayer(): DefaultAudioPlayerManager {
            val playerManager = DefaultAudioPlayerManager()

            playerManager.configuration
                .setFrameBufferFactory(::NonAllocatingAudioFrameBuffer)

            AudioSourceManagers.registerRemoteSources(playerManager)
            return playerManager
        }
    }

    data class ManagersModule(val appConfig: AppConfig): KoinModules {
        override fun shouldInitialize(): Boolean = true

        override fun initialize(): Module = module {
            single { RssManager() }
            single { DiscordUserManager() }
            single { MtgManager() }
            single { PokemonManager() }
            single { PriceChartingManager() }
        }

    }
}