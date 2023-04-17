package com.portalsoup.saas.data.tables.rss

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate


object RssSubscriptionTable: IntIdTable("rss_subscription") {
    val createdOn = date("created_on")
    val feedUrl = varchar("feed_url", 255)
    val nickname = varchar("nickname", 50)
    val guildId = varchar("guild_id", 50)
    val active = bool("active")
}

data class RssSubscription(
    val id: Int,
    val active: Boolean,
    val createdOn: LocalDate,
    val feedUrl: String,
    val nickname: String,
    val guildId: String
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = RssSubscription(
            id = resultRow[RssSubscriptionTable.id].value,
            active = resultRow[RssSubscriptionTable.active],
            createdOn = resultRow[RssSubscriptionTable.createdOn],
            feedUrl = resultRow[RssSubscriptionTable.feedUrl],
            nickname = resultRow[RssSubscriptionTable.nickname],
            guildId = resultRow[RssSubscriptionTable.guildId]
        )
    }
}