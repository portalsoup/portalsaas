package com.portalsoup.saas.discord.command

import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

/**
 * Simply responds "Pong!"
 */
object PingPongCommand: IDiscordCommand {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        return event.message
            .channel
            .flatMap { it.createMessage("Pong!") }
            .then()
    }
}