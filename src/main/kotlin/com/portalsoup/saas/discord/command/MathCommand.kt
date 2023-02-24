package com.portalsoup.saas.discord.command

import com.notkamui.keval.Keval
import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

/**
 * Evaluate a math expression and return the float result.
 *
 * For example:
 *   !math 2 + 2
 */
object MathCommand: IDiscordCommand {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val expression: String = event.message.content.split("!math").lastOrNull()?.trim() ?: return Mono.empty()

        val result = kotlin.runCatching {
            Keval {
                includeDefault()
            }.eval(expression)
        }

        return if (result.isFailure) {
            event.message.channel
                .flatMap { it.createMessage("I didn't understand that") }
                .then()
        } else {
            event.message.channel
                .flatMap { it.createMessage(result.getOrThrow().toString()) }
                .then()
        }

    }
}