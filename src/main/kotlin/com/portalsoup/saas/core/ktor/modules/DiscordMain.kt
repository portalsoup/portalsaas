package com.portalsoup.saas.core.ktor.modules

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.discord.DiscordClientBuilder
import com.portalsoup.saas.discord.DiscordBot
import com.portalsoup.saas.manager.MtgManager
import io.ktor.server.application.*

//val foo by lazy { KoinPlatformTools.defaultContext().get().get<AppConfig>() }
fun Application.discord(appConfig: AppConfig) {

    if (appConfig.discordToken?.isNotEmpty() == true) {
        DiscordClientBuilder.build(appConfig)
        log.info("Initializing discord bot...")
        DiscordBot().init()
        log.info("Discord bot ready to go")
    }

    val mtgManager = MtgManager()

    mtgManager.updateMtgSetsData()
}
