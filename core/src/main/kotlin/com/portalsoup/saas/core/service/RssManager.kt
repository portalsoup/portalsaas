package com.portalsoup.saas.core.service

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import java.net.URL
import java.util.*

data class RssFeed(val feedUrl: String, val name: String)
data class RssSubscription(val discordUserId: String, val rssFeed: RssFeed, val guildId: String)
data class RssSubscriptionEntry(val created: Date, val rssSubscription: RssSubscription, val discordUserId: String)


object RssManager {

    fun getFeed(url: URL): SyndFeed {
        val feedInput = SyndFeedInput()
        val feed: SyndFeed = feedInput.build(XmlReader(url))

//        feed.entries.map { it. }

        println(feed)
        return feed
    }
}

fun main() {
    val url = URL("https://blog.jetbrains.com/kotlin/feed/")
    val syndFeed = RssManager.getFeed(url)

    val feed = RssFeed(syndFeed.link, syndFeed.title)
    val sub = RssSubscription("", feed, "")
    val readEntries = mutableListOf<RssSubscriptionEntry>()

    syndFeed.entries
        .take(5)
        .onEach { readEntries.add(RssSubscriptionEntry(it.publishedDate, sub, "")) }

    println(syndFeed.entries.size)
    println(syndFeed.entries.filterNot { readEntries.contains(RssSubscriptionEntry(it.publishedDate, sub, "")) }.size)
}