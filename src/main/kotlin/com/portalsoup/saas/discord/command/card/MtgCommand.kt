package com.portalsoup.saas.discord.command.card

import com.portalsoup.saas.manager.MtgManager
import com.portalsoup.saas.discord.command.IDiscordCommand
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Mono

/**
 * Look up a magic the gathering card from scryfall and display it's photo and details.
 */
object MtgCommand: IDiscordCommand {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val term = event.message.content.split("!mtg").lastOrNull() ?: return Mono.empty()

       return runBlocking { MtgManager().embed(term) }
            ?.let { embed ->
                event.message.channel
                    .flatMap { it.createMessage(embed) }
                    .then()
            } ?: event.message.channel
                .flatMap { it.createMessage("I couldn't find a matching card") }
                .then()
    }
}