package com.portalsoup.saas.discord.command.application.global

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.reactivestreams.Publisher

interface ApplicationCommand {

    fun handleEvent(event: ChatInputInteractionEvent): Publisher<Void>?
}