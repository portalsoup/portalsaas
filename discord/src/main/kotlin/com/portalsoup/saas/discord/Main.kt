package com.portalsoup.saas.discord


import com.portalsoup.saas.config.AppConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

val appConfig = loadConfig()

fun main() = appConfig.discord.token
    ?.takeIf { it.isNotEmpty() }
    ?.let { DiscordClientBuilder.build() }
    ?.let { DiscordBot(it, appConfig) }
    ?.init()
    ?: throw MissingApplicationPropertyException("Missing discord API token from properties file!")

@OptIn(ExperimentalSerializationApi::class)
private fun loadConfig(): AppConfig = AppConfig::class.java.getResourceAsStream(AppConfig.PROPS_PATH)
    ?.let { Json.decodeFromStream(it) }
    ?: throw RuntimeException("Application properties file not found.")

data class MissingApplicationPropertyException(override val message: String, val reason: Throwable? = null): RuntimeException(message, reason)