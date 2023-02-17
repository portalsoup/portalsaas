package com.portalsoup.saas.discord.command.card

import com.portalsoup.saas.api.card.MagicApi
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Mono

object MtgCommand: Command {
    override fun execute(event: MessageCreateEvent): Mono<Void> {
        val term = event.message.content.split("!mtg").lastOrNull() ?: return Mono.empty()

        val cardEmbed = runBlocking {
            MagicApi().embed(term)
        }

        return event.message.channel
            .flatMap { it.createMessage(cardEmbed) }
            .then()
    }
}