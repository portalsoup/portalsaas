package com.portalsoup.saas.discord.command.gpx

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.discord.command.AbstractDiscordSlashCommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.utils.FileUpload
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.random.Random

object RenderGpxMapCommand : AbstractDiscordSlashCommand(), Logging {

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

            invokePythonScript("scripts/generate_map/generate_map.py")

//            val image = OpenMapsManager().renderMap()
//            val bufferedImage = BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB)

//            val imgFile = File.createTempFile("renderedGpx${System.currentTimeMillis()}", ".jpg")
//                ImageIO.write(image, "JPG", imgFile)
//
            event.hook.sendFiles(
                FileUpload.fromData(File("scripts/generate_map/map.jpg"))
            ).queue()
        }
    }

    fun invokePythonScript(scriptPath: String) {
        val processBuilder = ProcessBuilder("python", scriptPath)
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            // Process the output of the Python script
            println(line)
        }

        val exitCode = process.waitFor()
        println("Python script executed with exit code: $exitCode")
    }
}
