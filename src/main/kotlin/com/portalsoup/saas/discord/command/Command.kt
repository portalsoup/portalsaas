package com.portalsoup.saas.discord.command

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

interface Command: Logging {
    fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void>

    fun fail(reason: String): Mono<Void> {
        log().info(reason)
        return Mono.empty()
    }
}