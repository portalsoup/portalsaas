package com.portalsoup.saas.manager

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.data.tables.rss.RssFeed
import com.portalsoup.saas.data.tables.rss.RssFeedTable
import discord4j.core.GatewayDiscordClient
import io.ktor.client.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class RssManager: Logging, KoinComponent {

    private val httpClient by inject<HttpClient>()
    private val discordClient by inject<GatewayDiscordClient>()

//    fun fetchFeed(user: String, nickname: String): SyndFeed {
//    }

    fun addSubscription(guild: String, user: String, feedUrl: String, nickname: String) {
        val feed: RssFeed? = transaction {
            RssFeedTable.select { RssFeedTable.feedUrl eq feedUrl }
                .firstOrNull()
                ?.let { RssFeed.fromRow(it) }
        }

        if (feed == null) {
            transaction {
                RssFeedTable.insert {
                    it[RssFeedTable.feedUrl] = feedUrl
                    it[RssFeedTable.createdOn] = LocalDate.now()
                }
            }
        }
    }

//    fun removeSubscription(user: String, nickname: String) {
//    }
//
//    fun listSubscriptions(user: String): List<String> {
//    }

//    private fun getOrInsertFeed(feedUrl: String): RssFeed {
//        val maybeFeed = transaction {
//            RssFeedTable.select { RssFeedTable.feedUrl eq feedUrl }
//                .firstOrNull()
//                ?.let { RssFeed.fromRow(it) }
//        }
//
//        if (maybeFeed == null) {
//            transaction {
//                RssFeedTable.insert {
//                    it[RssFeedTable.feedUrl] = feedUrl
//                    it[RssFeedTable.createdOn] = LocalDate.now()
//                }
//            }
//        }
//    }
}