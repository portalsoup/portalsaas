package com.portalsoup.saas.discord.command.friendcode

import com.portalsoup.saas.data.tables.FriendCode
import com.portalsoup.saas.data.tables.FriendCodeTable
import com.portalsoup.saas.discord.command.AbstractDiscordSlashCommand
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.koin.core.component.KoinComponent
import java.time.LocalDate

/**
 * Retrieve your or another user's friend code.
 *
 * Retrieving another user's friend code requires a @mention on that user, this restricts the bot to only look up
 * users that share the common server with the requester.
 */
object FriendCodeCommand: KoinComponent, AbstractDiscordSlashCommand() {

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

            val response: String = transaction {
                when (action) {
                    OPTIONS.GET -> { lookupCode(user) }
                    OPTIONS.ADD -> { addCode(user, code) }
                    OPTIONS.REMOVE -> {removeCodesForUser(user) }
                    OPTIONS.UPDATE -> { updateCode(user, code) }
                }
            }

            event.hook.sendMessage(response).queue()
        }
    }

    private fun lookupCode(user: User) =
        FriendCode.find { FriendCodeTable.user eq user.id }
            .singleOrNull()
            ?.code
            ?: "I didn't find that Friend Code"

    private fun removeCodesForUser(user: User): String {
        FriendCodeTable.deleteWhere { FriendCodeTable.user eq user.id }
        return "Removed"
    }

    private fun addCode(user: User, code: String?): String {
        val userMaybeExists = FriendCodeTable
            .select { FriendCodeTable.user eq user.id }
            .firstOrNull()

        return if (userMaybeExists != null) {
            "I already have your code, use the \"update\" action if you want to change it"
        } else if (code == null) {
            "The code you provided appears to be invalid"
        } else {
            FriendCodeTable.insert {
                it[FriendCodeTable.user] = user.id
                it[FriendCodeTable.code] = code
                it[createdOn] = LocalDate.now()
            }
            "I added your friend code to my database"
        }
    }

    private fun updateCode(user: User, code: String?): String {
        if (code == null) {
            return "That's an invalid code"
        }
        FriendCodeTable.update({FriendCodeTable.user eq user.id }) {
            it[FriendCodeTable.code] = code
        }
        return "Updated your code"
    }
    private fun validateFriendCodeFormat(code: String?): Boolean =
        code?.takeIf { friendCodeRegex.matches(code) } != null
}