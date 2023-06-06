package com.portalsoup.saas.discord.command.friendcode

import com.portalsoup.saas.data.tables.FriendCode
import com.portalsoup.saas.data.tables.FriendCodeTable
import com.portalsoup.saas.discord.command.IDiscordGlobalCommand
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import java.time.LocalDate

/**
 * Retrieve your or another user's friend code.
 *
 * Retrieving another user's friend code requires an @mention on that user, this restricts the bot to only look up
 * users that share the common server with the requester.
 */
object FriendCodeCommand: KoinComponent, IDiscordGlobalCommand() {

    override val commandData: CommandData = Commands.slash("friendcode", "manage your switch friend code.")
        .addOption(OptionType.MENTIONABLE, "user", "The user whose code to look up.")
        .addOption(OptionType.BOOLEAN, "remove", "Remove your own Friend Code from the database.")
        .addOption(OptionType.STRING, "add  ", "Add your Friend Code to the database if it doesn't already exist.")

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (isMatch(event)) {
            event.deferReply().setEphemeral(true).queue()

            val user = event.getOption("user")?.asUser ?: event.user
            val remove = event.getOption("remove")?.asBoolean ?: false
            val code = event.getOption("add")?.asString ?: ""

            if (remove) {
                removeUser(user)
            }

            if (code.isNotEmpty()) {
                addCode(user, event)
            }

            val maybeFoundCode: String = transaction {
                FriendCodeTable.select { FriendCodeTable.user eq user.id }.singleOrNull()
            }
                ?.let { FriendCode.fromRow(it).code }
                ?: "I didn't find a relevant Friend Code."

            event.hook.sendMessage(maybeFoundCode).queue()
        }
    }

    private fun removeUser(user: User) {
        transaction {
            FriendCodeTable.deleteWhere { FriendCodeTable.user eq user.id }
        }
    }

    private fun addCode(user: User, event: SlashCommandInteractionEvent) {
        val userMaybeExists = transaction {
            FriendCodeTable.select { FriendCodeTable.user eq user.id }
                .firstOrNull()
        }

        if (userMaybeExists != null) {
            return event.hook.sendMessage("I already have your code").queue()
        } else {

            transaction {
                FriendCodeTable.insert {
                    it[FriendCodeTable.user] = user.id
                    it[FriendCodeTable.code] = code
                    it[createdOn] = LocalDate.now()
                }
            }

            event.hook.sendMessage("I added your friend code to my database.")
        }
    }
}