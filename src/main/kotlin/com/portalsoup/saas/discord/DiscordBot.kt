package com.portalsoup.saas.discord

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.discord.command.IDiscordCommand
import com.portalsoup.saas.discord.command.DiceRollCommand
import com.portalsoup.saas.discord.command.MathCommand
import com.portalsoup.saas.discord.command.youtube.JoinVoiceCommand
import com.portalsoup.saas.discord.command.PingPongCommand
import com.portalsoup.saas.discord.command.card.MtgCommand
import com.portalsoup.saas.discord.command.friendcode.AddFriendCodeCommand
import com.portalsoup.saas.discord.command.friendcode.LookupFriendCodeCommand
import com.portalsoup.saas.discord.command.friendcode.RemoveFriendCodeCommand
import com.portalsoup.saas.discord.command.pokedex.PokedexCommand
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

/**
 * The main Discord bot entrypoint.  This class should only be instantiated when appConfig.discordToken is provided.
 */
class DiscordBot: KoinComponent, Logging {

    private val appConfig by inject<AppConfig>()

    private val commands = hashMapOf<String, IDiscordCommand>()

    private val client: GatewayDiscordClient = DiscordClient
        .create(appConfig.discordToken ?: throw RuntimeException("The discord bot should not have been initialized"))
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

    /**
     * This is the bot entrypoint
     */
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

    /**
     * Set up the commands hashmap so that received events can be matched to behavior.
     */
    private fun initCommands() {

        commands["ping"] = PingPongCommand
        commands["math"] = MathCommand
        commands["join"] = JoinVoiceCommand
        commands["play"] = PlayYoutubeCommand
        commands["friendcode"] = LookupFriendCodeCommand
        commands["friendcode add"] = AddFriendCodeCommand
        commands["friendcode remove"] = RemoveFriendCodeCommand
        commands["mtg"] = MtgCommand
        commands["pokedex"] = PokedexCommand
        commands["vg"] = VideoGameLookupCommand
        commands["roll"] = DiceRollCommand
    }

    companion object {
        const val COMMAND_PREFIX: String = "!"
    }

    private fun String.commandPrefix() = COMMAND_PREFIX + this
}