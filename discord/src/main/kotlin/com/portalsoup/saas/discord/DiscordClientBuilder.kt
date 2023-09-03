package com.portalsoup.saas.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder

object DiscordClientBuilder {

    fun build(): JDA = JDABuilder.createDefault(appConfig.discord.token).build()

}