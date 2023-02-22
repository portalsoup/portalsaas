package com.portalsoup.saas.discord.command.friendcode

import com.portalsoup.saas.data.tables.FriendCodeTable
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import reactor.core.publisher.Mono

object RemoveFriendCodeCommand: Command {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val user = event.message.author.orElse(null) ?: return Mono.empty()

        transaction {
            FriendCodeTable.select { FriendCodeTable.user eq user.id.asString() }
                .firstOrNull()
        } ?: return event.message.channel.flatMap {
            it.createMessage("I don't have your friend code..")
        }.then()

        transaction {
            FriendCodeTable.deleteWhere { FriendCodeTable.user eq user.id.asString() }
        }

        return event.message.channel
            .flatMap { it.createMessage("Deleted.") }
            .then()
    }
}