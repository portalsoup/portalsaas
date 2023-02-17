package com.portalsoup.saas.discord

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.Logging
import com.portalsoup.saas.core.log
import com.portalsoup.saas.discord.command.LavaPlayerAudioProvider
import com.portalsoup.saas.discord.command.TrackScheduler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.spec.GuildCreateFields.PartialChannel
import discord4j.discordjson.json.ChannelData
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


class DMusic: KoinComponent, Logging {

    val appConfig by inject<AppConfig>()
    val audioManager by inject<DefaultAudioPlayerManager>()
    val audioProvider by inject<LavaPlayerAudioProvider>()

    val commands = hashMapOf<String, (event: MessageCreateEvent) -> Mono<Void>>()

    val client: GatewayDiscordClient = DiscordClient.create(appConfig.discordToken)
        .gateway()
        .setEnabledIntents(IntentSet.of(
            Intent.DIRECT_MESSAGES,
            Intent.GUILD_MESSAGES,
            Intent.GUILDS,
            Intent.GUILD_VOICE_STATES
        ))
        .login()
        .block()
        ?: throw RuntimeException("Failed to connect to Discord")

    fun init() {
        log().info("Logged into Discord!")
        initCommands()

        client.eventDispatcher.on(MessageCreateEvent::class.java)
            .flatMap { event ->
                Mono.just(event.message.content)
                    .flatMap { content ->
                        Flux.fromIterable(commands.entries)
                            .filter { content.startsWith(COMMAND_PREFIX + it.key) }
                            .flatMap { it.value(event) }
                            .next()
                    }
            }
            .subscribe()
    }

    fun initCommands() {
        commands["ping"] = { event ->
            println("Receieved command!  [!ping]")
            event.message
                .channel
                .flatMap { it.createMessage("Pong!") }
                .then()
        }

        commands["join"] = { event ->
            println("Receieved command!  [!join]")
            Mono.justOrEmpty(event.member)
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

        val scheduler = TrackScheduler(audioProvider.player)
        commands["play"] = { event ->
            println("Receieved command!  [!play]")
            Mono.justOrEmpty(event.message.content)
                .map { it.split(" ") }
                .doOnNext { audioManager.loadItem(it[1], scheduler) }
                .then()
        }
    }

    companion object {
        const val COMMAND_PREFIX: String = "!"
    }

}