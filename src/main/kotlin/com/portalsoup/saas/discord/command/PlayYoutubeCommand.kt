package com.portalsoup.saas.discord.command

import com.portalsoup.saas.discord.TrackScheduler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

class PlayYoutubeCommand(private val audioManager: DefaultAudioPlayerManager, private val scheduler: TrackScheduler): Command {
    override fun execute(event: MessageCreateEvent): Mono<Void> {
        println("Receieved command!  [!play]")
        return Mono.justOrEmpty(event.message.content)
            .map { it.split(" ") }
            .doOnNext { audioManager.loadItem(it[1], scheduler) }
            .then()
    }
}