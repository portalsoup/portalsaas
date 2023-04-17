package com.portalsoup.saas.data.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object DiscordUserTable: IntIdTable("discord_user") {
    val createdOn = date("created_on")
    val updatedOn = date("updated_on")
    val snowflake = varchar("snowflake", 50)
    val nickname = varchar("nickname", 50).nullable()
    val dmGuildId = varchar("dm_guild_id", 50)
}

data class DiscordUser(
    val id: Int,
    val createdOn: LocalDate,
    val updatedOn: LocalDate,
    val snowflake: String,
    val nickname: String?,
    val dmGuildId: String
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = DiscordUser(
            id = resultRow[DiscordUserTable.id].value,
            createdOn = resultRow[DiscordUserTable.createdOn],
            updatedOn = resultRow[DiscordUserTable.updatedOn],
            snowflake = resultRow[DiscordUserTable.snowflake],
            nickname = resultRow[DiscordUserTable.nickname],
            dmGuildId = resultRow[DiscordUserTable.dmGuildId]
        )
    }
}