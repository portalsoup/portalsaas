package com.portalsoup.saas.manager

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.data.tables.RssSubscription
import com.portalsoup.saas.data.tables.RssSubscriptionTable
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.PartialMember
import discord4j.core.spec.EmbedCreateSpec
import discord4j.discordjson.json.gateway.RequestGuildMembers
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

object RssManager: Logging, KoinComponent {

    private val httpClient by inject<HttpClient>()
    private val discordClient by inject<GatewayDiscordClient>()

    fun rssPoller() {
        log().info("RSS is polling")
        runBlocking {
            transaction {
                RssSubscriptionTable.selectAll().toList()
                    .map { RssSubscription.fromRow(it) }
                    .onEach { launch { processSubscription(it) } }
            }
        }
    }

    private fun processSubscription(subscription: RssSubscription) {
        val feed = fetchFeed(subscription.user, subscription.nickname)
        discordClient
            .takeIf { feed.publishedDate.toInstant().atZone(ZoneId.of("Z")).toLocalDate() >= subscription.updatedOn }
            ?.requestMembers(
                RequestGuildMembers.builder().guildId(subscription.guildId).limit(1)
                    .addUserId(subscription.user)
                    .build()
            )
            ?.map  { sendFeedToUser(feed, it, subscription) }
            ?.subscribe()

    }

    private fun sendFeedToUser(feed: SyndFeed, member: PartialMember, subscription: RssSubscription) {
        log().info("Sending RSS to user...")
        log().info(feed.toString())
        member.privateChannel.subscribe { channel ->
            createRssSpecs(subscription, feed).takeIf { it.second.isNotEmpty() }
                ?.also { channel.createMessage(*it.second).subscribe() }
                ?.also { embeds ->
                    embeds.first
                        .takeIf { it != null }
                        ?.let {date ->
                            transaction {
                                RssSubscriptionTable.update({ RssSubscriptionTable.id eq subscription.id }) {
                                    it[updatedOn] = date
                                }
                            }
                        }
                }
        }
    }

    private fun createRssSpecs(subscription: RssSubscription, feed: SyndFeed): Pair<LocalDate?, Array<EmbedCreateSpec>> {
        log().info("[RSS FEED]About to start generating rss messages")
        val entries =  feed.entries
            .onEach { log().info("PERFORMING A CALC! ${subscription.updatedOn.toEpochSecond(LocalTime.now(), ZoneOffset.UTC)} < ${it.publishedDate.time / 1000}") }
            .filter { subscription.updatedOn.toEpochSecond(LocalTime.now(), ZoneOffset.UTC) < it.publishedDate.time / 1000 }
            .also { log().info("[RSS FEED]Filtered out old messages, left with ${it.size} entries")}
            .sortedBy { it.publishedDate }
            .also { log().info("[RSS FEED]Sorted and took the first ${it.size} items") }
            .take(10)

        val embeds = entries
            .map {
                EmbedCreateSpec.builder()
                    .author(it.author, null, null)
                    .footer(DateFormat.getDateInstance(DateFormat.SHORT).format(it.publishedDate), null)
                    .title(it.title)
                    .url(it.uri)
                    .image(feed.image.url)
                    .description(it.description.value)
                    .build()
            }
            .toTypedArray()

        return Pair(
            entries.lastOrNull()?.publishedDate?.toInstant()?.atZone(ZoneId.of("Z"))?.toLocalDate(), // UTF
            embeds
        )
    }

    fun fetchFeed(user: String, nickname: String): SyndFeed {
        val url = transaction { RssSubscriptionTable
            .select { (RssSubscriptionTable.user eq user) and (RssSubscriptionTable.nickname eq nickname) }
            .firstOrNull()
            ?.let { RssSubscription.fromRow(it) }
        }?.feedUrl ?: throw RuntimeException("Failed to find a feed url")

        return runBlocking {
            val response = httpClient.get(url)
            SyndFeedInput().build(response.bodyAsChannel().toInputStream().bufferedReader())
        }
    }

    fun addSubscription(guild: String, user: String, url: String, nickname: String) {
        val now = LocalDate.now()
        log().info("Logging a new subscription: $guild   $user   $nickname")
        transaction {
            RssSubscriptionTable.insert {
                it[active] = true
                it[createdOn] = now
                it[updatedOn] = now.minusDays(180)
                it[feedUrl] = url
                it[RssSubscriptionTable.user] = user
                it[RssSubscriptionTable.nickname] = nickname
                it[guildId] = guild
            }
        }
    }

    fun removeSubscription(user: String, nickname: String) {
        transaction {
            RssSubscriptionTable.deleteWhere { (RssSubscriptionTable.user eq user) and (RssSubscriptionTable.nickname eq nickname) }
        }
    }

    fun listSubscriptions(user: String): List<String> {
        log().info("Getting a list of subscriptions: $user")
        return transaction {
            RssSubscriptionTable.select { RssSubscriptionTable.user eq user }
                .map { RssSubscription.fromRow(it) }
                .map { "${it.nickname}: ${it.feedUrl}" }
        }
    }
}