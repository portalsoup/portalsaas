package com.portalsoup.saas.data.tables.rss

import com.portalsoup.saas.data.tables.DiscordUserTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object RssSubscriptionDiscordUsersTable: IntIdTable("rss_subscription") {
    val createdOn = date("created_on")
    val rssSubscriptionId = reference("rss_subscription_id", RssSubscriptionTable.id)
    val discordUserId = reference("discord_user_id", DiscordUserTable.id)
}