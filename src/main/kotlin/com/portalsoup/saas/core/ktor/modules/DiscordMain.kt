package com.portalsoup.saas.core.ktor.modules

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.discord.DiscordClientBuilder
import com.portalsoup.saas.discord.DiscordBot
import io.ktor.server.application.*

//val foo by lazy { KoinPlatformTools.defaultContext().get().get<AppConfig>() }
fun Application.discord() {
    val appConfig = AppConfig.default(environment)

    if (appConfig.discordToken?.isNotEmpty() == true) {
        DiscordClientBuilder.build(appConfig)
        log.info("Initializing discord bot...")
        DiscordBot().init()
        log.info("Discord bot ready to go")

    }
}
