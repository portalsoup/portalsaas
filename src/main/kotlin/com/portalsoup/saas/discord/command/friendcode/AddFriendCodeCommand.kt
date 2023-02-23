package com.portalsoup.saas.discord.command.friendcode

import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.data.tables.FriendCodeTable
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import reactor.core.publisher.Mono
import java.time.LocalDate

object AddFriendCodeCommand: Command, KoinComponent {

    private val friendCodeRegex = Regex("(SW-\\d{4}-\\d{4}-\\d{4})")

    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {

        val message = event.message
        val content = message.content ?: return fail("Failed to find the message content")
        val user = event.message.author.orElse(null) ?: return fail("Failed to find the message author")
        val code = parseCode(content) ?: return fail("Failed to find code in message [$message]")

        val userMaybeExists = transaction {
            FriendCodeTable.select { FriendCodeTable.user eq user.id.asString() }
                .firstOrNull()
        }

        if (userMaybeExists != null) {
            return message.channel
                .flatMap { it.createMessage("I already have your code") }
                .then()
        }

        transaction {
            FriendCodeTable.insert {
                it[FriendCodeTable.user] = user.id.asString()
                it[FriendCodeTable.code] = code
                it[createdOn] = LocalDate.now()
            }
        }

        return message.channel
            .flatMap { it.createMessage("I added your friend code!") }
            .then()
    }

    private fun parseCode(message: String): String? = friendCodeRegex
        .find(message)
        ?.also { log().info("Found the value: ${it.value}") }
        ?.value
}
