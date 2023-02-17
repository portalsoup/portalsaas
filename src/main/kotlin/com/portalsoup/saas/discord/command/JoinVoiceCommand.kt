package com.portalsoup.saas.discord.command

import com.portalsoup.saas.discord.LavaPlayerAudioProvider
import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

class JoinVoiceCommand(val audioProvider: LavaPlayerAudioProvider): Command {
    override fun execute(event: MessageCreateEvent): Mono<Void> {
        println("Receieved command!  [!join]")
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