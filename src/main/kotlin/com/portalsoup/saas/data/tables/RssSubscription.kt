package com.portalsoup.saas.data.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate


object RssSubscriptionTable: IntIdTable("rss_subscription") {
    val user = varchar("user_id", 50)
    val createdOn = date("created_on")
    val active = bool("active")
    val updatedOn = date("updated_on")
    val feedUrl = varchar("feed_url", 255)
    val nickname = varchar("nickname", 50)
    val guildId = varchar("guild_id", 50)
}

data class RssSubscription(
    val id: Int,
    val user: String,
    val active: Boolean,
    val createdOn: LocalDate,
    val updatedOn: LocalDate,
    val feedUrl: String,
    val nickname: String,
    val guildId: String
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = RssSubscription(
            id = resultRow[RssSubscriptionTable.id].value,
            user = resultRow[RssSubscriptionTable.user],
            active = resultRow[RssSubscriptionTable.active],
            createdOn = resultRow[RssSubscriptionTable.createdOn],
            updatedOn = resultRow[RssSubscriptionTable.updatedOn],
            feedUrl = resultRow[RssSubscriptionTable.feedUrl],
            nickname = resultRow[RssSubscriptionTable.nickname],
            guildId = resultRow[RssSubscriptionTable.guildId]
        )
    }
}