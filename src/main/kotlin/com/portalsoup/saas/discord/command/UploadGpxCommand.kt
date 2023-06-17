package com.portalsoup.saas.discord.command

import com.portalsoup.saas.core.extensions.Logging
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

object UploadGpxCommand : IDiscordSlashCommand(), Logging {

    override val commandData: CommandData = Commands.slash("upload-gpx", "Upload a gpx file")
        .addOption(OptionType.ATTACHMENT, "name", "Name of card", false)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        println("Got event")
        private(event) {
            println("Invoking payload")
            event.hook.sendMessage("Test response").queue()
        }
    }


}
