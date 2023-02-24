package com.portalsoup.saas.discord.command.youtube

import com.portalsoup.saas.discord.TrackScheduler
import com.portalsoup.saas.discord.command.IDiscordCommand
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import discord4j.core.event.domain.message.MessageCreateEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import reactor.core.publisher.Mono

/**
 * Instructs the bot to begin streaming audio from a youtube URL to the currently joined voice channel.
 */
object PlayYoutubeCommand: IDiscordCommand, KoinComponent {

    private val audioManager by inject<DefaultAudioPlayerManager>()
    private val scheduler by inject<TrackScheduler>()

    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        return Mono.justOrEmpty(event.message.content)
            .map { it.split(" ") }
            .doOnNext { audioManager.loadItem(it[1], scheduler) }
            .then()
    }
}