package com.portalsoup.saas.discord.command.pricecharting

import com.portalsoup.saas.extensions.Logging
import com.portalsoup.saas.extensions.log
import com.portalsoup.saas.db.dao.PriceChartingDAO
import com.portalsoup.saas.discord.command.AbstractDiscordSlashCommand
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

/**
 * Look up a video game by name on pricecharting.
 */
object VideoGameLookupCommand: AbstractDiscordSlashCommand(), Logging {

    override val commandData: CommandData = Commands.slash("vgprice", "manage your switch friend code.")
        .addOption(OptionType.STRING, "game", "The game name to look up.", true)

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val optionValue = event.focusedOption.value.lowercase()

    }
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        global(event) {

            val maxResults = 25
            val game = event.getOption("game")?.asString ?: throw RuntimeException("The game name is null, it is required")

            val foundGames = runCatching {
                PriceChartingDAO.lookupGamePrice(game, maxResults)
            }
                .onFailure { log().info("An exception was thrown! ${it.message}") }
                .getOrElse { emptyList() }
                .asSequence()
                .sortedBy { it.priceDate }
                .distinctBy { it.pricechartingId }
                .groupBy { it.name }
                .map { grouped -> FoundVideoGame(grouped.key, grouped.value.associate { it.console to it.price }) }
                .take(maxResults)
                .mapNotNull(VideoGameLookupCommand::formatGameString)
                .toList()
        }
    }
    /*
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
                .map(VideoGameLookupCommand::formatGameString)
                .collect(Collectors.joining("\n"))
                .flatMap { msg ->  event.message.channel.flatMap {
                    val msgPrefix = msg.takeUnless { m -> m.isEmpty() }?.let { "I found a few possible matches with loose prices:" } ?: "I didn't find any games that match"
                    it.createMessage("$msgPrefix\n${msg ?: ""}") } }
                .then()
                ?: event.message.channel.flatMap { it.createMessage("I couldn't find any games that matched") }.then()

    */
    private fun formatGameString(game: FoundVideoGame?): String? {
        val atLeastOneFound = { g: FoundVideoGame? -> g != null && g.consoles.size > 1 }

        val formatConsoleEntry = { console: Map.Entry<String, String?> -> "${console.key}: ${console.value ?: ""}"}
        val responsePrefix = { foundGame: FoundVideoGame -> "${foundGame.name} - ${foundGame.takeIf(atLeastOneFound)}" }

        return game
            .takeIf { it != null && atLeastOneFound(it) }
            ?.let {
                it.consoles.map(formatConsoleEntry).joinToString(
                    separator = "\n\t",
                    prefix = responsePrefix(it)
                )
            }
    }
}

data class FoundVideoGame(val name: String, val consoles: Map<String, String?>)
