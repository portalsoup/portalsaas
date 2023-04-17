package com.portalsoup.saas.data.tables.rss

import com.portalsoup.saas.data.tables.DiscordUserTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object RssSubscriptionReadEntriesTable: IntIdTable("rss_subscription") {
    val createdOn = date("created_on")
    val rssSubscriptionId = integer("rss_subscription_id").references(RssSubscriptionTable.id)
    val discordUserId = integer("discord_user_id").references(DiscordUserTable.id)
}

data class RssSubscriptionReadEntries(
    val id: Int,
    val createdOn: LocalDate,
    val rssSubscriptionId: Int,
    val discordUserId: Int
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = RssSubscriptionReadEntries(
            id = resultRow[RssSubscriptionReadEntriesTable.id].value,
            createdOn = resultRow[RssSubscriptionReadEntriesTable.createdOn],
            rssSubscriptionId = resultRow[RssSubscriptionReadEntriesTable.rssSubscriptionId],
            discordUserId = resultRow[RssSubscriptionReadEntriesTable.discordUserId]
        )
    }
}