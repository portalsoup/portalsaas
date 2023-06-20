package com.portalsoup.saas.discord.command.gpx

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.discord.command.IDiscordSlashCommand
import com.portalsoup.saas.service.GPXManager
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import java.io.File
import kotlin.random.Random

object UploadGpxCommand : IDiscordSlashCommand(), Logging {

    override val commandData: CommandData = Commands.slash("upload-gpx", "Upload a gpx file")
        .addOption(OptionType.ATTACHMENT, "file", "GPX file", false)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        private(event) {
            event.hook.sendMessage("Test response").queue()
            val tmpFile = File.createTempFile("gpx", Random.nextInt().toString())

            event.getOption("file")
                ?.asAttachment
                ?.proxy
                ?.downloadToFile(tmpFile)
                ?.join()

            GPXManager.importGpxFile(tmpFile)
        }
    }


}
