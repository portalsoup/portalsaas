package com.portalsoup.saas.discord.command.pricecharting

import com.portalsoup.saas.core.Logging
import com.portalsoup.saas.core.db.execAndMap
import com.portalsoup.saas.core.log
import com.portalsoup.saas.data.tables.pricecharting.VideoGame
import com.portalsoup.saas.data.tables.pricecharting.VideoGamePrice
import com.portalsoup.saas.data.tables.pricecharting.VideoGamePriceTable
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

object VideoGameLookupCommand: Command, Logging {

    private const val resultLimit = 10

    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val message = event.message

        return kotlin.runCatching { transaction {
            // Raw query is required here because of the trigrams extension
            """
                |SELECT *, product_name <<-> '$truncatedMessage' as dist
                |   FROM video_game
                |   ORDER BY dist
                |   LIMIT $resultLimit
            """.trimMargin().execAndMap { VideoGame.fromSet(it) }
        }
        }
            .getOrElse { emptyList() }
            .let { Flux.fromIterable(it) } // It is now a Flux
            .map(::formatGameString)
            .collect(Collectors.joining("\n"))
            .flatMap { msg ->  message.channel.flatMap {
                val msgPrefix = msg.takeUnless { m -> m.isEmpty() }?.let { "I found a couple game matches:" } ?: "I didn't find any games that match"
                it.createMessage("$msgPrefix\n${msg}") } }
            .then()
            ?: message.channel.flatMap { it.createMessage("I couldn't find any games that matched") }.then()
    }

    private fun formatGameString(game: VideoGame): String =
        "${game.productName} - ${game.consoleName} ${getPriceOfGame(game)?.loosePrice?.let { ": $it" } ?: ""}"

    private fun getPriceOfGame(game: VideoGame): VideoGamePrice? = transaction {
        game.id
            .let { VideoGamePriceTable.select { VideoGamePriceTable.videoGameId eq it } }
            .sortedBy { VideoGamePriceTable.createdOn }
            .map(VideoGamePrice::fromRow)
            .also { println("Got the prices of the following game:\n ${it.joinToString("\n")}") }
            .firstOrNull()
    }

    fun embed(game: VideoGame): EmbedCreateSpec {
        return EmbedCreateSpec.builder()
            .title(game.productName)
            .addField("Console", game.consoleName, false)
//            .addField("Loose price", game.)
            .build()
    }

    fun gameNotFound(it: MessageChannel): Mono<Void> {
        return it.createMessage("").then()
    }
}

data class FoundVideoGame(val name: String, val consoles: HashMap<String, String?>)

/*

SELECT
  DISTINCT vg.pricecharting_id,
  vg.product_name <-> 'chrono trigger' as dist,
  vg.product_name,
  vg.console_name,
  loose_price
FROM
  video_game as vg
  LEFT OUTER JOIN video_game_price ON video_game_price.video_game_id = vg.id
ORDER by dist LIMIT 25;

*/