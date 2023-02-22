package com.portalsoup.saas.discord.command.card

import com.portalsoup.saas.manager.card.MagicManager
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Mono

object MtgCommand: Command {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val term = event.message.content.split("!mtg").lastOrNull() ?: return Mono.empty()

        val cardEmbed = runBlocking {
            MagicManager().embed(term)
        }

        return event.message.channel
            .flatMap { it.createMessage(cardEmbed) }
            .then()
    }
}