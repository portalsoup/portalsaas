package com.portalsoup.saas.data.tables.rss

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date


object RssFeedTable: IntIdTable("rss_subscription") {
    val createdOn = date("created_on")
    val feedUrl = varchar("feed_url", 255).uniqueIndex()
}


class RssFeed(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RssFeed>(RssFeedTable)

    var createdOn by RssFeedTable.createdOn
    var feedUrl by RssFeedTable.feedUrl
}