package com.portalsoup.saas.discord.command

import com.portalsoup.saas.discord.TrackScheduler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import discord4j.core.event.domain.message.MessageCreateEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import reactor.core.publisher.Mono

object PlayYoutubeCommand: Command, KoinComponent {

    private val audioManager by inject<DefaultAudioPlayerManager>()
    private val scheduler by inject<TrackScheduler>()

    override fun execute(event: MessageCreateEvent): Mono<Void> {
        println("Received command!  [!play]")
        return Mono.justOrEmpty(event.message.content)
            .map { it.split(" ") }
            .doOnNext { audioManager.loadItem(it[1], scheduler) }
            .then()
    }
}