package com.portalsoup.saas.manager

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.data.tables.DiscordUser
import com.portalsoup.saas.data.tables.DiscordUserTable
import discord4j.common.util.Snowflake
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.InteractionReplyEditSpec
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.reactivestreams.Publisher
import java.time.LocalDate

class DiscordUserManager: KoinComponent, Logging {

    fun lookupUser(snowflake: Snowflake): DiscordUser? {
        return transaction {
            DiscordUserTable.select { DiscordUserTable.snowflake eq snowflake.asString() }
                .firstOrNull()
                ?.let { DiscordUser.fromRow(it) }
        }
    }

    fun addUser(snowflake: Snowflake, dmGuildId: Snowflake) {
        val now = LocalDate.now()
        transaction {
            DiscordUserTable.insert {
                it[createdOn] = now
                it[updatedOn] = now
                it[DiscordUserTable.snowflake] = snowflake.asString()
                it[nickname] = null
                it[DiscordUserTable.dmGuildId] = dmGuildId.asString()
            }
        }
    }

    fun addIfNewUser(userSnowflake: Snowflake, dmGuildId: Snowflake) {
        log().info("Checking for the presence of a discord user...")
        if (lookupUser(userSnowflake) == null) {
            log().info("New discord user found!")
            addUser(userSnowflake, dmGuildId)
        }
    }

    fun whoami(event: ChatInputInteractionEvent): Publisher<Message>? {
        val user = lookupUser(event.interaction.user.id)
        event.deferReply().withEphemeral(true)

        return if (user != null) {

            return event.editReply(
                InteractionReplyEditSpec.builder()
                    .build()
                    .withContentOrNull("You are ${user.snowflake}")
            )
        } else event.editReply(
            InteractionReplyEditSpec.builder()
                .build()
                .withContentOrNull("I don't know who you are")
        )
    }
}