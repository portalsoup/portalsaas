package com.portalsoup.saas


import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.discord.DiscordClientBuilder
import com.portalsoup.saas.discord.DiscordBot

fun main(args: Array<String>) {
    if (AppConfig.discord.token?.isNotEmpty() == true) {
        val client = DiscordClientBuilder.build()
        DiscordBot(client).init()
    }
}
