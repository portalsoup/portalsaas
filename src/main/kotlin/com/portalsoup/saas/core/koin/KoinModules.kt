package com.portalsoup.saas.core.koin

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.discord.DiscordClientBuilder
import com.portalsoup.saas.service.PriceChartingManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
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
            single { HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    })
                }
            } }
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
        override fun shouldInitialize() = appConfig.discord.token.isNullOrEmpty().not()
        override fun initialize(): Module {
            val client: JDA = DiscordClientBuilder.build(appConfig)
            client.awaitReady()

            val userProp = appConfig.discord.userID
            val guildProp = appConfig.discord.guildID

            val guild = client.getGuildById(guildProp) ?: throw RuntimeException("No guild specified in props")
//            val user = client.getUserById(userProp) ?: throw RuntimeException("No user specified in props")

            return module {
                single<JDA> { client }
//                single<User> { user }
                single<Guild> { guild }

            }
        }
    }

    data class LavaPlayerModule(val appConfig: AppConfig): KoinModules {
        override fun shouldInitialize(): Boolean = true

        override fun initialize(): Module  {
            val playerManager = initLavaPlayer()
            val player = playerManager.createPlayer()

            return module {
//                single { LavaPlayerAudioProvider(player) }
//                single { TrackScheduler(player) }
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
            single { PriceChartingManager() }
        }

    }
}