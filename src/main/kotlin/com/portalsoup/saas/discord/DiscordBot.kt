package com.portalsoup.saas.discord

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.discord.command.MathCommand
import net.dv8tion.jda.api.JDA
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * The main Discord bot entrypoint.  This class should only be instantiated when appConfig.discordToken is provided.
 */
class DiscordBot: KoinComponent, Logging {

    private val client by inject<JDA>()

    /**
     * This is the bot entrypoint
     */
    fun init() {
        client.updateCommands()
            .addCommands(
            MathCommand.commandData
        ).queue()

        client.addEventListener(MathCommand)

    }
}