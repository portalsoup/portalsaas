package com.portalsoup.saas.discord.command.friendcode

import com.portalsoup.saas.discord.command.AbstractDiscordSlashCommand
import com.portalsoup.saas.core.service.FriendCodeManager
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

/**
 * Retrieve your or another user's friend code.
 *
 * Retrieving another user's friend code requires a @mention on that user, this restricts the bot to only look up
 * users that share the common server with the requester.
 */
object FriendCodeCommand: AbstractDiscordSlashCommand() {

    /**
     * Match the format of a switch friend code: SW-####-####-####
     */
    private val friendCodeRegex: Regex = "SW-[0-9]{4}-[0-9]{4}-[0-9]{4}".toRegex()
    enum class OPTIONS { GET, ADD, REMOVE, UPDATE }

    override val commandData: CommandData = Commands.slash("friend-code", "manage your switch friend code.")
        .addOption(OptionType.MENTIONABLE, "user", "The user whose code to look up.")
        .addOption(OptionType.STRING, "code", "Add your Friend Code to the database if it doesn't already exist.")
        .addOption(OptionType.STRING, "action", "What action would you like to perform?")

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val optionValue = event.focusedOption.value.lowercase()

        OPTIONS.values()
            .takeIf { isAutocompleteMatch(event, "action") }
            ?.map { it.name.lowercase() }
            ?.filter { it.startsWith(optionValue) }
            ?.take(25) // There aren't 25 options, but this is an observed limit on the discord side
            ?.let { event.replyChoiceStrings(it).queue() }
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        global(event) {
            event.deferReply().setEphemeral(true).queue()

            val user = event.getOption("user")?.asUser ?: event.user
            val code = event.getOption("code")?.asString?.takeIf { validateFriendCodeFormat(it) }
            val action = event.getOption("action")?.asString?.uppercase()
                ?.let { OPTIONS.valueOf(it) }
                ?: OPTIONS.GET

            val response: String = when (action) {
                OPTIONS.GET -> { FriendCodeManager.lookupFriendCode(user) }
                OPTIONS.ADD -> { FriendCodeManager.addFriendCode(user, code) }
                OPTIONS.REMOVE -> { FriendCodeManager.removeFriendCode(user) }
                OPTIONS.UPDATE -> { FriendCodeManager.updateFriendCode(user, code) }
            }

            event.hook.sendMessage(response).queue()
        }
    }

    private fun validateFriendCodeFormat(code: String?): Boolean =
        code?.takeIf { friendCodeRegex.matches(code) } != null
}