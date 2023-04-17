package com.portalsoup.saas.data.tables.rss

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate


object RssFeedTable: IntIdTable("rss_subscription") {
    val createdOn = date("created_on")
    val feedUrl = varchar("feed_url", 255).uniqueIndex()
}

data class RssFeed(
    val id: Int,
    val createdOn: LocalDate,
    val feedUrl: String,
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = RssFeed(
            id = resultRow[RssSubscriptionTable.id].value,
            createdOn = resultRow[RssSubscriptionTable.createdOn],
            feedUrl = resultRow[RssSubscriptionTable.feedUrl],
        )
    }
}