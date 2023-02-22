package com.portalsoup.saas.discord.command

import com.notkamui.keval.Keval
import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

object MathCommand: Command {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val expression: String = event.message.content.split("!math").lastOrNull()?.trim() ?: return Mono.empty()

        val result = kotlin.runCatching {
            Keval {
                includeDefault()

                constant {
                    name = "nice"
                    value = 69.0
                }

                constant {
                    name = "dank"
                    value = 420.0
                }

            }.eval(expression)
        }

        return if (result.isFailure) {
            event.message.channel
                .flatMap { it.createMessage("That's not real math") }
                .then()
        } else {
            event.message.channel
                .flatMap { it.createMessage(result.getOrThrow().toString()) }
                .then()
        }

    }
}