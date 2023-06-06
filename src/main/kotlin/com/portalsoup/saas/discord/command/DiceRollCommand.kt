package com.portalsoup.saas.discord.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import kotlin.random.Random

/**
 * Roll a number of die and display the results.  If the quantity and faces are omitted, then 1D20 is default.
 *
 * Example:
 *   !roll 5D100
 */
object DiceRollCommand: IDiscordGlobalCommand() {
    override val commandData: CommandData = Commands.slash("roll", "Roll a quantity of die")
        .addOption(OptionType.INTEGER, "sides", "The amount of sides does dice has")
        .addOption(OptionType.INTEGER, "quantity", "The amount of die to throw")

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (isMatch(event)) {
            event.deferReply().queue()

            val sides = event.getOption("sides")?.asInt ?: 20
            val quantity = event.getOption("quantity")?.asInt ?: 1

//            event.hook.sendMessage("").queue()

            EmbedBuilder()
                .setTitle("Results from dice throw${quantity.takeIf { it > 1 }?.let { "s" } ?: ""}...")
                .setDescription(buildTitle(event.user.name, quantity, sides))
                .also {
                    for (i in 1..quantity) {
                        val roll = (Random.nextInt(sides) + 1).toString()
                        it.addField("", roll, true)
                    }
                }
                .build()
                .let { event.hook.sendMessageEmbeds(it).queue() }
        }
    }

    private fun buildTitle(author: String?, die: Int, faces: Int): String {
        val authorPrefixOrCapitalR = author?.let { "$it r" } ?: "R"
        return "${authorPrefixOrCapitalR}olled $die D${faces}${die.takeIf { it > 1 }?.let { "s" } ?: ""}"
    }
}