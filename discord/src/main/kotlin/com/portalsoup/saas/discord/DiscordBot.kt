package com.portalsoup.saas.discord

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.discord.command.DiceRollCommand
import com.portalsoup.saas.discord.command.MathCommand
import com.portalsoup.saas.discord.command.card.MtgCommand
import com.portalsoup.saas.discord.command.friendcode.FriendCodeCommand
import com.portalsoup.saas.discord.command.gpx.RenderGpxMapCommand
import com.portalsoup.saas.discord.command.gpx.UploadGpxCommand
import com.portalsoup.saas.extensions.Logging
import net.dv8tion.jda.api.JDA
import java.time.OffsetDateTime
import java.time.ZoneId


/**
 * The main Discord bot entrypoint.  This class should only be instantiated when appConfig.discordToken is provided.
 */
class DiscordBot(private val client: JDA, private val appConfig: AppConfig): Logging {
    /**
     * This is the bot entrypoint
     */
    fun init() {

        val guild = client.getGuildById(appConfig.discord.guild.id)

        guild
            ?.getTextChannelById(appConfig.discord.guild.vipId)
            ?.sendMessage(wakeupMessage())
            ?.queue()

        guild
            ?.updateCommands()
            ?.addCommands(
                UploadGpxCommand.commandData
            )?.queue()

        client.updateCommands()
            .addCommands(
                MathCommand.commandData,
                DiceRollCommand.commandData,
                FriendCodeCommand.commandData,
                MtgCommand.commandData,
                RenderGpxMapCommand.commandData
            ).queue()


        client.addEventListener(MathCommand)
        client.addEventListener(DiceRollCommand)
        client.addEventListener(FriendCodeCommand)
        client.addEventListener(MtgCommand)
        client.addEventListener(UploadGpxCommand)
        client.addEventListener(RenderGpxMapCommand)

    }

    private fun wakeupMessage(): String {
        return when (OffsetDateTime.now(ZoneId.of("UTC-07:00")).hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
    }
}