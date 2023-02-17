package com.portalsoup.saas.discord.command

import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

interface Command {
    fun execute(event: MessageCreateEvent): Mono<Void>
}