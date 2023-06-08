package com.portalsoup.saas.discord.command

import com.portalsoup.saas.core.extensions.Logging
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData

abstract class IDiscordCommand: Logging, ListenerAdapter() {

}

abstract class IDiscordGlobalCommand: IDiscordCommand() {

    abstract val commandData: CommandData

    fun isMatch(event: SlashCommandInteractionEvent): Boolean {
        return event.name == commandData.name
    }
}