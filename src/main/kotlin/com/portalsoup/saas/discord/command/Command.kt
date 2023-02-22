package com.portalsoup.saas.discord.command

import com.portalsoup.saas.core.Logging
import com.portalsoup.saas.core.log
import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

interface Command: Logging {
    fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void>

    fun fail(reason: String): Mono<Void> {
        log().info(reason)
        return Mono.empty()
    }
}