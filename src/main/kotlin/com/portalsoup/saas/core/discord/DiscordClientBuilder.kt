package com.portalsoup.saas.core.discord

import com.portalsoup.saas.config.AppConfig
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet

object DiscordClientBuilder {

    fun build(appConfig: AppConfig): GatewayDiscordClient = DiscordClient
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

}