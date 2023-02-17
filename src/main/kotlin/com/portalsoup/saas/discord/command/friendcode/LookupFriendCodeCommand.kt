package com.portalsoup.saas.discord.command.friendcode

import com.portalsoup.saas.data.tables.FriendCode
import com.portalsoup.saas.data.tables.FriendCodeTable
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.User
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import reactor.core.publisher.Mono
import kotlin.jvm.optionals.getOrNull

object LookupFriendCodeCommand: KoinComponent, Command {
    override fun execute(event: MessageCreateEvent): Mono<Void> {
        val message = event.message
        val content = message.content ?: return fail("Failed to find the message content")
        val user: User = event.message.userMentions.firstOrNull()
            ?: event.message.author.orElse(null)
            ?: return fail("Failed to find any referencable user to lookup")

        val foundUser = transaction {
            Mono.just(user)
                .map { FriendCodeTable.select { FriendCodeTable.user eq it.id.asString() } }
                .map { it.single() }
                .map { FriendCode.fromRow(it) }
                .map { "${user.username}: ${it.code}" }
        }

        foundUser.subscribe { msg ->
            message.channel.flatMap { it.createMessage(msg) }
        }

        return foundUser.then()
    }

}