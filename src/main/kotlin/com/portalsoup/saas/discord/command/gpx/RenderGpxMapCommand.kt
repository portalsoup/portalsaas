package com.portalsoup.saas.discord.command.gpx

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.discord.command.IDiscordSlashCommand
import com.portalsoup.saas.service.OpenMapsManager
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.utils.FileUpload
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random

object RenderGpxMapCommand : IDiscordSlashCommand(), Logging {

    override val commandData: CommandData = Commands.slash("render-gpx", "Render a gpx file")
        .addOption(OptionType.ATTACHMENT, "file", "GPX file", false)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        private(event) {
            val tmpFile = File.createTempFile("gpx", Random.nextInt().toString())

            event.getOption("file")
                ?.asAttachment
                ?.proxy
                ?.downloadToFile(tmpFile)
                ?.join()

            println("RENDERING IMAGE!")
            val image = OpenMapsManager().renderMap()
//            val bufferedImage = BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB)

            val imgFile = File.createTempFile("renderedGpx${System.currentTimeMillis()}", ".jpg")
            ImageIO.write(image, "JPG", imgFile)

            event.hook.sendFiles(
                FileUpload.fromData(imgFile)
            ).queue()
        }
    }


}
