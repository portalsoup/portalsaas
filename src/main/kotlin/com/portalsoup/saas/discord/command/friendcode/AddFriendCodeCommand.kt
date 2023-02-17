package com.portalsoup.saas.discord.command.friendcode

import com.portalsoup.saas.core.Logging
import com.portalsoup.saas.core.log
import com.portalsoup.saas.data.tables.FriendCode
import com.portalsoup.saas.data.tables.FriendCodeTable
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.User
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import reactor.core.publisher.Mono
import java.time.LocalDate

object AddFriendCodeCommand: Command, KoinComponent {

    private val friendCodeRegex = Regex("(SW-\\d{4}-\\d{4}-\\d{4})")

    override fun execute(event: MessageCreateEvent): Mono<Void> {
        val message = event.message
        val content = message.content ?: return fail("Failed to find the message content")
        val user = event.message.author.orElse(null) ?: return fail("Failed to find the message author")
        val code = parseCode(content) ?: return fail("Failed to find code in message [$message]")

        val codeContainer =  Mono.justOrEmpty(event.message)
            .map { FriendCodeContainer(parseCode(content), user) }
            .then()

        codeContainer.subscribe { reply("Found the code and user!  $user: $code", event.message) }


        val userMaybeExists = transaction {
            FriendCodeTable.select { FriendCodeTable.user eq user.id.asString() }
                .firstOrNull()
        }

        if (userMaybeExists != null) {
            reply("I already have your code", message)
            return Mono.empty()
        }

        transaction {
            Mono.create<FriendCodeTable> {
                FriendCodeTable.insert {
                    it[FriendCodeTable.user] = user.id.asString()
                    it[FriendCodeTable.code] = code
                    it[createdOn] = LocalDate.now()
                }
            }
        }

        return codeContainer
    }

    private fun parseCode(message: String): String? = friendCodeRegex
        .find(message)
        ?.also { log().info(it.value) }
        ?.value

    private fun reply(message: String, event: Message) = event.channel
        .also { log().info("message") }
        .flatMap { it.createMessage(message) }
}

data class FriendCodeContainer(val code: String?, val user: User)