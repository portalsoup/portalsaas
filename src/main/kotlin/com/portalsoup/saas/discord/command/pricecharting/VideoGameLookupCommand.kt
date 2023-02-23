package com.portalsoup.saas.discord.command.pricecharting

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.db.execAndMap
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.discord.command.Command
import discord4j.core.event.domain.message.MessageCreateEvent
import org.jetbrains.exposed.sql.transactions.transaction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.stream.Collectors

object VideoGameLookupCommand: Command, Logging {

    private const val resultLimit = 50
    private const val discordResultLimit = 3L

    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> =
        runCatching {
            transaction {
                // Raw query is required here because of the trigrams extension
                """
                |SELECT
                |  vg.pricecharting_id,
                |  vg.product_name || vg.console_name <-> '$truncatedMessage' as dist,
                |  vg.product_name,
                |  vg.console_name,
                |  loose_price,
                |  video_game_price.created_on as price_date
                |FROM
                |  video_game as vg
                |  LEFT OUTER JOIN video_game_price ON video_game_price.video_game_id = vg.pricecharting_id
                |ORDER by dist, price_date LIMIT $resultLimit;
            """.trimMargin().execAndMap { Result(
                    name = it.getString("product_name"),
                    console = it.getString("console_name"),
                    price = it.getString("loose_price"),
                    pricechartingId = it.getString("pricecharting_id"),
                    priceDate = it.getDate("price_date").toLocalDate()
                )
                }
            }
        }
            .onFailure { log().info("An exception was thrown! ${it.message}") }
            .getOrElse { emptyList() }
            .sortedBy { it.priceDate }
            .distinctBy { it.pricechartingId }
            .groupBy { it.name }
            .map { grouped -> FoundVideoGame(grouped.key, grouped.value.associate { it.console to it.price }) }
            .let { Flux.fromIterable(it) } // It is now a Flux
            .take(discordResultLimit)
            .map(::formatGameString)
            .collect(Collectors.joining("\n"))
            .flatMap { msg ->  event.message.channel.flatMap {
                val msgPrefix = msg.takeUnless { m -> m.isEmpty() }?.let { "I found a few possible matches with loose prices:" } ?: "I didn't find any games that match"
                it.createMessage("$msgPrefix\n${msg ?: ""}") } }
            .then()
            ?: event.message.channel.flatMap { it.createMessage("I couldn't find any games that matched") }.then()


    private fun formatGameString(game: FoundVideoGame): String = "" +
            "${game.name} - ${takeIf { game.consoles.size > 1 }?.let { "\n\t" } ?: "" }" +
            game.consoles.map { "${it.key}: ${it.value ?: ""}" }.joinToString("\n\t")
}

data class Result(val pricechartingId: String, val name: String, val console: String, val price: String?, val priceDate: LocalDate)

data class FoundVideoGame(val name: String, val consoles: Map<String, String?>)
