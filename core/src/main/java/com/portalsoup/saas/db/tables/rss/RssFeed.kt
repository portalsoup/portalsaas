package com.portalsoup.saas.db.tables.rss

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date


object RssFeedTable: IntIdTable("rss_subscription") {
    val createdOn = date("created_on")
    val feedUrl = varchar("feed_url", 255).uniqueIndex()
    val name = varchar("name", 255)
}


class RssFeed(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RssFeed>(RssFeedTable)

    var createdOn by RssFeedTable.createdOn
    var feedUrl by RssFeedTable.feedUrl
    var name by RssFeedTable.name
}