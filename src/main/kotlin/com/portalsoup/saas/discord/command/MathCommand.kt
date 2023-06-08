package com.portalsoup.saas.discord.command

import com.notkamui.keval.Keval
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

/**
 * Evaluate a math expression and return the float result.
 *
 * For example:
 *   !math 2 + 2
 */
object MathCommand: IDiscordGlobalCommand() {

    override val commandData: CommandData = Commands.slash("math", "Evaluate a mathematical expression")
        .addOption(OptionType.STRING, "expression", "A mathematical expression", true)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (isMatch(event)) {
            event.deferReply().queue()

            val expression = event.getOption("expression")?.asString ?: return

            val result = kotlin.runCatching {
                Keval {
                    includeDefault()
                }.eval(expression)
            }


            if (result.isFailure) {
                event.hook.sendMessage("I didn't understand that expression").queue()
            } else {
                event.hook.sendMessage(result.getOrThrow().toString()).queue()
            }
        }
    }
}