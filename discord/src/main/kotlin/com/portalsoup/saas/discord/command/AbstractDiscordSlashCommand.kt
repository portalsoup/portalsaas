package com.portalsoup.saas.discord.command

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.discord.command.pricecharting.VideoGameLookupCommand
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData

abstract class AbstractDiscordSlashCommand: ListenerAdapter() {

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

    private fun isMatch(event: SlashCommandInteractionEvent): Boolean = event.name == commandData.name

    fun isAutocompleteMatch(event: CommandAutoCompleteInteractionEvent, option: String) =
        event.name == VideoGameLookupCommand.commandData.name && event.focusedOption.name == option

    fun x(event: CommandAutoCompleteInteractionEvent, preserveCase: Boolean = false): String {
        val optionValue = event.focusedOption.value
        return when {
            preserveCase -> optionValue
            else -> optionValue.lowercase()
        }
    }
}


sealed class Scope(val event: SlashCommandInteractionEvent) {

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
        return AppConfig.discord.userID == event.user.id
    }
}
class Guild(event: SlashCommandInteractionEvent): Scope(event) {
    override fun shouldRun(event: SlashCommandInteractionEvent): Boolean {
        return AppConfig.discord.guildID == (event.guild?.id ?: return false)
    }
}