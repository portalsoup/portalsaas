package com.portalsoup.saas.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object DiscordUserTable: IntIdTable("discord_user") {
    val createdOn = date("created_on")
    val updatedOn = date("updated_on")
    val snowflake = varchar("snowflake", 50)
    val nickname = varchar("nickname", 50).nullable()
    val dmGuildId = varchar("dm_guild_id", 50)
}

class DiscordUser(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<DiscordUser>(DiscordUserTable)

    var createdOn by DiscordUserTable.createdOn
    var updatedOn by DiscordUserTable.updatedOn
    var snowflake by DiscordUserTable.snowflake
    var nickname by DiscordUserTable.nickname
    var dmGuildId by DiscordUserTable.dmGuildId
}