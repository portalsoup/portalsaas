package com.portalsoup.saas.discord.command.youtube

import com.portalsoup.saas.discord.LavaPlayerAudioProvider
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import reactor.core.publisher.Mono

object JoinVoiceCommand: Command, KoinComponent {

    private val audioProvider by inject<LavaPlayerAudioProvider>()
    override fun execute(event: MessageCreateEvent): Mono<Void> {
        println("Received command!  [!join]")
        return Mono.justOrEmpty(event.member)
            .flatMap {
                println("Getting voice state")
                it.voiceState
            }
            .flatMap {
                println("Getting channel")
                it.channel
            }
            .flatMap {
                println("Joining with audio provider")
                it.join().withProvider(audioProvider)
            }
            .then()
    }
}