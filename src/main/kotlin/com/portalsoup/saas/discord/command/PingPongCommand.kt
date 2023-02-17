package com.portalsoup.saas.discord.command

import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

class PingPongCommand: Command {
    override fun execute(event: MessageCreateEvent): Mono<Void> {
        println("Receieved command!  [!ping]")
        return event.message
            .channel
            .flatMap { it.createMessage("Pong!") }
            .then()
    }
}