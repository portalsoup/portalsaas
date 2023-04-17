package com.portalsoup.saas.discord

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.discord.command.DiceRollCommand
import com.portalsoup.saas.discord.command.IDiscordCommand
import com.portalsoup.saas.discord.command.MathCommand
import com.portalsoup.saas.discord.command.PingPongCommand
import com.portalsoup.saas.discord.command.card.MtgCommand
import com.portalsoup.saas.discord.command.friendcode.AddFriendCodeCommand
import com.portalsoup.saas.discord.command.friendcode.LookupFriendCodeCommand
import com.portalsoup.saas.discord.command.friendcode.RemoveFriendCodeCommand
import com.portalsoup.saas.discord.command.pokedex.PokedexCommand
import com.portalsoup.saas.discord.command.pricecharting.VideoGameLookupCommand
import com.portalsoup.saas.discord.command.youtube.JoinVoiceCommand
import com.portalsoup.saas.discord.command.youtube.PlayYoutubeCommand
import com.portalsoup.saas.manager.DiscordUserManager
import com.portalsoup.saas.manager.MtgManager
import com.portalsoup.saas.manager.RssManager
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * The main Discord bot entrypoint.  This class should only be instantiated when appConfig.discordToken is provided.
 */
class DiscordBot: KoinComponent, Logging {

    private val appConfig by inject<AppConfig>()
    private val mtgManager by inject<MtgManager>()
    private val rssManager by inject<RssManager>()
    private val discordUserManager by inject<DiscordUserManager>()

    private val commands = hashMapOf<String, IDiscordCommand>()

    private val client by inject<GatewayDiscordClient>()

    /**
     * This is the bot entrypoint
     */
    fun init() {

        log().info("Logged into Discord!")
        initCommands()
        initGlobalCommandDefinitions()
        initGlobalCommands()

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


    private fun initGlobalCommands() {

        client.on(ChatInputInteractionEvent::class.java) { event ->
            event.interaction
                .also { log().info("Found an interaction, does it have a guild id?") }
                .takeIf { it.guildId.isPresent }
                ?.also { log().info("Found an interaction with a guild id") }
                ?.also { discordUserManager.addIfNewUser(event.interaction.user.id, it.guildId.get()) }

            event.reply()
        }.subscribe()

        client.on(ChatInputInteractionEvent::class.java) { event ->
            event.takeIf { it.commandName == "whoami" }?.let { discordUserManager.whoami(event) }
        }.subscribe()


        client.on(ChatInputInteractionEvent::class.java) { event ->
            event.takeIf { it.commandName == "greet" }?.reply("Hello")
        }.subscribe()

        client.on(ChatInputInteractionEvent::class.java) { event ->
            event.takeIf { it.commandName == "mtg" }?.reply("")
        }.subscribe()
//
//        client.on(ChatInputInteractionEvent::class.java) { event ->
//            event.takeIf { it.commandName == "rss-add" && event.interaction.guildId.isPresent }
//                ?.also {
//                    rssManager.addSubscription(
//                        event.interaction.guildId.get().asString(),
//                        event.interaction.user.id.asString(),
//                        getUrlFromEvent(it),
//                        getNicknameFromEvent(it)
//                    ) }
//                ?.reply("Got it, I'll let you know when this feed is updated.")
//                ?.withEphemeral(true)
//        }.subscribe()
//
//        client.on(ChatInputInteractionEvent::class.java) { event ->
//            event.takeIf { it.commandName == "rss-delete" }
//                ?.also { rssManager.removeSubscription(event.interaction.user.id.asString(), getNicknameFromEvent(it)) }
//                ?.reply("Subscription removed..")
//                ?.withEphemeral(true)
//        }.subscribe()
//
//        client.on(ChatInputInteractionEvent::class.java) { event ->
//            event.takeIf { it.commandName == "rss-list" }
//                ?.reply("Subscriptions:\n" + rssManager.listSubscriptions(event.interaction.user.id.asString()).joinToString("\n"))
//                ?.withEphemeral(true)
//        }.subscribe()

    }

    private fun getNicknameFromEvent(event: ChatInputInteractionEvent): String {
        return event.getOption("nickname")
            .flatMap { it.value }
            .map { it.asString() }
            .get()
    }

    private fun getUrlFromEvent(event: ChatInputInteractionEvent): String {
        return event.getOption("url")
            .flatMap { it.value }
            .map { it.asString() }
            .get()
    }

    private fun initGlobalCommandDefinitions() {
        val commands = listOf(
            "greet",
            "rss-add",
            "rss-delete",
            "rss-list",
            "whoami"
        )

        runCatching {
            CommandReader(client.restClient).init(commands)
        }
            .onFailure { log().error("There was an error registering global commands!", it) }
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