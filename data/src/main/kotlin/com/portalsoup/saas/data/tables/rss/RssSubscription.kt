package com.portalsoup.saas.data.tables.rss

import com.portalsoup.saas.data.tables.DiscordUser
import com.portalsoup.saas.data.tables.DiscordUserTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date


object RssSubscriptionTable: IntIdTable("rss_subscription") {
    val createdOn = date("created_on")
    val feedUrl = varchar("feed_url", 255)
    val nickname = varchar("nickname", 50)
    val guildId = varchar("guild_id", 50)
    val active = bool("active")
    val discordUserId = reference("discord_user_id", DiscordUserTable.id)
    val rssFeedId = reference("rss_feed_id", RssFeedTable.id)
}

class RssSubscription(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<RssSubscription>(RssSubscriptionTable)

    var createdOn by RssSubscriptionTable.createdOn
    var feedUrl by RssSubscriptionTable.feedUrl
    var nickname by RssSubscriptionTable.nickname
    var guildId by RssSubscriptionTable.guildId
    var active by RssSubscriptionTable.active

    var discordUsers by DiscordUser via RssSubscriptionDiscordUsersTable

}