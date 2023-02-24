package com.portalsoup.saas.discord.command.youtube

import com.portalsoup.saas.discord.LavaPlayerAudioProvider
import com.portalsoup.saas.discord.command.IDiscordCommand
import discord4j.core.event.domain.message.MessageCreateEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import reactor.core.publisher.Mono

/**
 * Instruct the bot to join the voice channel you are in if able.
 */
object JoinVoiceCommand: IDiscordCommand, KoinComponent {

    private val audioProvider by inject<LavaPlayerAudioProvider>()
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        return Mono.justOrEmpty(event.member)
            .flatMap { it.voiceState }
            .flatMap { it.channel }
            .flatMap { it.join().withProvider(audioProvider) }
            .then()
    }
}