package com.portalsoup.saas.db.tables.rss

import com.portalsoup.saas.db.tables.DiscordUser
import com.portalsoup.saas.db.tables.DiscordUserTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object RssSubscriptionDiscordUsersTable: IntIdTable("rss_subscription") {
    val createdOn = date("created_on")
    val rssSubscriptionId = reference("rss_subscription_id", RssSubscriptionTable.id)
    val discordUserId = reference("discord_user_id", DiscordUserTable.id)
}

class RssSubscriptionDiscordUsers(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RssSubscriptionDiscordUsers>(RssSubscriptionDiscordUsersTable)

    var createdOn by RssSubscriptionDiscordUsersTable.createdOn
    var rssSubscription by RssSubscriptionDiscordUsersTable.rssSubscriptionId
    var discordUsers by DiscordUser via RssSubscriptionDiscordUsersTable

}