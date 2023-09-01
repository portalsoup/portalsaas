package com.portalsoup.saas.discord.command.card

import com.portalsoup.saas.extensions.Logging
import com.portalsoup.saas.extensions.log
import com.portalsoup.saas.data.tables.scryfall.SetType
import com.portalsoup.saas.discord.command.AbstractDiscordSlashCommand
import com.portalsoup.saas.service.MtgManager
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

/**
 * Look up a magic the gathering card from scryfall and display its photo and details.
 */
object MtgCommand: AbstractDiscordSlashCommand(), Logging {

    val mtgManager = MtgManager()

    override val commandData: CommandData = Commands.slash("mtg", "Lookup a card")
        .addOption(OptionType.STRING, "name", "Name of card", true)
        .addOption(OptionType.STRING, "set", "The set of the card", false, true)


    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        global(event) {
            event.deferReply().queue()

            val term = event.getOption("name")?.asString
            val set = event.getOption("set")?.asString

            if (term == null) {
                event.hook.sendMessage("Didn't catch that card name").queue()
                return@global
            }

            val maybeFoundCard = runBlocking {
                mtgManager.embed(term, set)
            }

            maybeFoundCard?.let { event.hook.sendMessageEmbeds(it).queue() }
                ?: event.hook.sendMessage("I didn't find that card")
        }
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        if (event.name == "mtg" && event.focusedOption.name == "set") {
                val sets =
                runBlocking { mtgManager.getSetsAutocomplete() }
                    .filter { it.setType == SetType.EXPANSION || it.setType == SetType.CORE || it.setType == SetType.COMMANDER }
                    .map { it.name }
                    .filter { it.lowercase().startsWith(event.focusedOption.value.lowercase()) }
            log().info("Got a list of sets to autocomplete...: $sets")

            event.replyChoiceStrings(sets.take(25)).queue()
        }
    }
}