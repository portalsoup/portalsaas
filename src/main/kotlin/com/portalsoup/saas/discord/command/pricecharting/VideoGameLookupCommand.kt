package com.portalsoup.saas.discord.command.pricecharting

import com.portalsoup.saas.core.Logging
import com.portalsoup.saas.core.db.execAndMap
import com.portalsoup.saas.core.log
import com.portalsoup.saas.data.tables.pricecharting.VideoGame
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import org.jetbrains.exposed.sql.transactions.transaction
import reactor.core.publisher.Mono

object VideoGameLookupCommand: Command, Logging {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val message = event.message

        val maybeGame: VideoGame? = kotlin.runCatching { transaction {
            log().info("About to run query!")
            """
                |SELECT product_name, '$truncatedMessage' <-> product_name as dist
                |   FROM video_game
                |   ORDER BY dist LIMIT 10
            """.trimMargin().execAndMap {
                log().info("Query ran, processing a result....")
                val serialized = VideoGame.fromSet(it)
                log().info(serialized.toString())
                serialized

            }
        }
        }.getOrNull()?.firstOrNull()

        return message.channel
            .takeIf { maybeGame != null }
            ?.flatMap { it.createMessage(embed(maybeGame!!)) }
            ?.then()
            ?: message.channel.flatMap { it.createMessage("I didn't find any games similar enough to that") }.then()
    }

    fun embed(game: VideoGame): EmbedCreateSpec {
        return EmbedCreateSpec.builder()
            .title(game.productName)
            .addField("Console", game.consoleName, false)
            .build()
    }

    fun gameNotFound(it: MessageChannel): Mono<Void> {
        return it.createMessage("").then()
    }
}