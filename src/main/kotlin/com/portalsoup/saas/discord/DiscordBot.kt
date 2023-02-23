package com.portalsoup.saas.discord

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.Logging
import com.portalsoup.saas.core.log
import com.portalsoup.saas.discord.command.Command
import com.portalsoup.saas.discord.command.DiceRollCommand
import com.portalsoup.saas.discord.command.MathCommand
import com.portalsoup.saas.discord.command.youtube.JoinVoiceCommand
import com.portalsoup.saas.discord.command.PingPongCommand
import com.portalsoup.saas.discord.command.card.MtgCommand
import com.portalsoup.saas.discord.command.friendcode.AddFriendCodeCommand
import com.portalsoup.saas.discord.command.friendcode.LookupFriendCodeCommand
import com.portalsoup.saas.discord.command.friendcode.RemoveFriendCodeCommand
import com.portalsoup.saas.discord.command.pricecharting.VideoGameLookupCommand
import com.portalsoup.saas.discord.command.youtube.PlayYoutubeCommand
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class DiscordBot: KoinComponent, Logging {

    private val appConfig by inject<AppConfig>()

    val commands = hashMapOf<String, Command>()

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
                            .filter { content.startsWith(it.key.commandPrefix()) }
                            .flatMap { runCatching {
                                it.value.execute(event, event.message.content.removePrefix(it.key.commandPrefix()).trim())
                            }.getOrElse { Mono.empty() } }
                            .next()
                    }
            }
            .subscribe()
    }

    fun initCommands() {

        commands["ping"] = PingPongCommand
        commands["math"] = MathCommand
        commands["join"] = JoinVoiceCommand
        commands["play"] = PlayYoutubeCommand
        commands["friendcode"] = LookupFriendCodeCommand
        commands["friendcode add"] = AddFriendCodeCommand
        commands["friendcode remove"] = RemoveFriendCodeCommand
        commands["mtg"] = MtgCommand
//        commands["pokedex"] = PokedexCommand
        commands["vg"] = VideoGameLookupCommand
        commands["roll"] = DiceRollCommand
    }

    companion object {
        const val COMMAND_PREFIX: String = "!"
    }

    fun String.commandPrefix() = COMMAND_PREFIX + this
}