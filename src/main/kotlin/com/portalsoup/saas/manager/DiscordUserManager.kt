package com.portalsoup.saas.manager

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.data.tables.DiscordUser
import com.portalsoup.saas.data.tables.DiscordUserTable
import discord4j.common.util.Snowflake
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
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

    fun whoami(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono? {
        val user = lookupUser(event.interaction.user.id)

        return if (user != null) {
            event.reply()
                .withEphemeral(true)
                .withEmbeds(EmbedCreateSpec.builder()
                    .title(user.snowflake)
                    .addField("Nickname?", user.takeIf { it.nickname != null }?.let { "Yes" } ?: "No", true)
                    .addField("DM Snowflake", user.dmGuildId, true)
                    .build())
        } else event.reply().withEphemeral(true)
    }
}