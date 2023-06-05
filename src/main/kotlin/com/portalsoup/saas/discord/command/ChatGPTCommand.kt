package com.portalsoup.saas.discord.command

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.discord.DiscordChatGPT
import discord4j.core.event.domain.message.MessageCreateEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import reactor.core.publisher.Mono

object ChatGPTCommand: IDiscordCommand, KoinComponent {

    val appConfig: AppConfig by inject()
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val authorId = event.message.author.orElseGet(null)?.id?.asString()

        if (appConfig.myDiscordID == authorId) {
            return event.message
                .channel
                .flatMap { it.createMessage(DiscordChatGPT.gpt(truncatedMessage)) }
                .then() ?: event.message.channel.then()
        }

        return event.message.channel.then()
    }
}