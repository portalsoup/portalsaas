package com.portalsoup.saas.core.discord

import com.portalsoup.saas.config.AppConfig
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder

object DiscordClientBuilder {

    fun build(appConfig: AppConfig): JDA = JDABuilder.createDefault(appConfig.discordToken).build()

}