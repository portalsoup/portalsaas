package com.portalsoup.saas.discord.command.friendcode

import com.portalsoup.saas.data.tables.FriendCode
import com.portalsoup.saas.data.tables.FriendCodeTable
import com.portalsoup.saas.discord.command.IDiscordCommand
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.User
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import reactor.core.publisher.Mono

object LookupFriendCodeCommand: KoinComponent, IDiscordCommand {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val message = event.message
        val user: User = event.message.userMentions.firstOrNull()
            ?: event.message.author.orElse(null)
            ?: return fail("Failed to find any referencable user to lookup")

        return Mono.just(user)
            .mapNotNull { transaction { FriendCodeTable.select { FriendCodeTable.user eq it.id.asString() }.singleOrNull() } }
            .map { FriendCode.fromRow(it!!) } // This actually won't be null because of mapNotNull's terminating case
            .map { it.code }
            .flatMap { msg -> message.channel.flatMap { it.createMessage(msg) } }
            .then()
    }

}