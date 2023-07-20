package com.portalsoup.saas.discord.command

import com.portalsoup.saas.config.AppConfig
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class AbstractDiscordSlashCommand: ListenerAdapter(), KoinComponent {

    val appConfig by inject<AppConfig>()

    abstract val commandData: CommandData

    fun private(event: SlashCommandInteractionEvent, l: ListenerAdapter.() -> Unit) {
        println("In private wrapper")
        if (isMatch(event)) {
            println("Matched event")
            Private(event)() {
                this.l()
            }
        }
    }

    fun guild(event: SlashCommandInteractionEvent, l: () -> Unit) {
        if (isMatch(event)) Guild(event)(l)
    }

    fun global(event: SlashCommandInteractionEvent, l: () -> Unit) {
        if (isMatch(event)) l()
    }

    private fun isMatch(event: SlashCommandInteractionEvent): Boolean {
        return event.name == commandData.name
    }
}


sealed class Scope(val event: SlashCommandInteractionEvent): KoinComponent {

    val appConfig by inject<AppConfig>()

    abstract fun shouldRun(event: SlashCommandInteractionEvent): Boolean
    internal operator fun invoke(l: () -> Unit) {
        event.deferReply().queue()
        l.takeIf { shouldRun(event) }
            .also { println("Should run? ${shouldRun(event)}") }
            ?.let { it() }
    }
}

class Private(event: SlashCommandInteractionEvent): Scope(event) {
    override fun shouldRun(event: SlashCommandInteractionEvent): Boolean {
        return appConfig.discord.userID == event.user.id
    }
}
class Guild(event: SlashCommandInteractionEvent): Scope(event) {
    override fun shouldRun(event: SlashCommandInteractionEvent): Boolean {
        return appConfig.discord.guildID == (event.guild?.id ?: return false)
    }
}