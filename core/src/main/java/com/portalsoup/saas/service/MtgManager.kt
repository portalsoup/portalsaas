package com.portalsoup.saas.service

import com.portalsoup.saas.db.tables.scryfall.MtgSet
import com.portalsoup.saas.db.tables.scryfall.MtgSetTable
import com.portalsoup.saas.db.tables.scryfall.SetType
import com.portalsoup.saas.extensions.*
import com.portalsoup.saas.web.Api
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Collect functionality to interact with scryfall's API
 */
class MtgManager: Logging {

    private val namedSearchUrl = "https://api.scryfall.com/cards/named"
    private val searchUrl = "https://api.scryfall.com/cards/search"
    private val setsUrl = "https://api.scryfall.com/sets"

    suspend fun search(term: String): MessageEmbed? {
        return null
    }

    /**
     * Search for a card by name and return a Discord embedded message object containing the found card's
     */
    suspend fun embed(term: String, set: String?): MessageEmbed? {
        val card = getRawCardAsync(term, set).await() ?: return null

        log().info(card.toString(4))

        val color = determineColor(card.safeGetJsonArray("color_identity")?.toList())
        val cardName = card.safeGetString("name") ?: ""
        val uri = card.safeGetString("scryfall_uri") ?: ""
        val costLabel = "Mana Cost"
        val cost = card.getString("mana_cost") ?: ""
        val oracleText = card.safeGetString("oracle_text") ?: ""
        val flavorText = card.safeGetString("flavor_text") ?: ""
        val cardImage = card.safeGetJSONObject("image_uris")?.safeGetString("large") ?: ""
        val artCrop = card.safeGetJSONObject("image_uris")?.safeGetString("art_crop") ?: ""
        val atkLabel = card.safeGetString("Power") ?: ""
        val atk = card.safeGetString("power") ?: ""
        val defLabel = card.safeGetString("Toughness") ?: ""
        val def = card.safeGetString("toughness") ?: ""
        val spellType = card.safeGetString("type_line") ?: ""


        return EmbedBuilder()
            .setColor(color)
            .setAuthor(cardName, uri, null)
            .setThumbnail(cardImage)
            .addField("", spellType, true)
            .addField("", oracleText, false)
            .addField(costLabel, cost, true)
            .addField(
                listOf(atkLabel, defLabel).filter { it.isNotEmpty() }.joinToString("/"),
                listOf(atk, def).filter { it.isNotEmpty() }.joinToString("/"),
                true
            )
            .setImage(artCrop)
            .setFooter(flavorText, null)
            .build()
    }

    suspend fun searchForCard(term: String): Deferred<JSONArray?> {
        return coroutineScope {
            async {
                runCatching { JSONObject(Api.makeRequest("$searchUrl?q=$term")) }.getOrNull()?.getJSONArray("data")
            }
        }
    }

    /**
     * Fetch a card by name from scryfall's API.  Scryfall supports fuzzy searching
     */
    private suspend fun getRawCardAsync(term: String, set: String?): Deferred<JSONObject?> = coroutineScope {
        val set = set?.let { transaction { MtgSet.find { MtgSetTable.name eq set }.firstOrNull() } }
        val setUrlPart = set?.let { "&set=${it.code}" } ?: ""
        async { runCatching { JSONObject(Api.makeRequest("$namedSearchUrl?fuzzy=$term$setUrlPart")) }.getOrNull() }
    }

    suspend fun getListOfCardSetsFromScryfall(): Deferred<JSONArray?> = coroutineScope {
        async {
            runCatching {
                JSONObject(Api.makeRequest(setsUrl)).getJSONArray("data")
            }.getOrNull()
        }
    }

    fun getSetsAutocomplete(prefix: String): List<String> =
        transaction {
            MtgSet.all()
                .filter { it.setType == SetType.EXPANSION || it.setType == SetType.CORE || it.setType == SetType.COMMANDER }
                .map { it.name }
                .filter { it.lowercase().startsWith(prefix.lowercase()) }
                .toList()
        }

    fun updateMtgSetsData() {
        log().info("Check mtg set data from scryfall.")

        val scrySets = runBlocking { getListOfCardSetsFromScryfall().await() } ?: return
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        
        for (i in 0..scrySets.length() - 1) {
            val current = scrySets.getJSONObject(i)
            if (transaction { MtgSetTable.select { MtgSetTable.code eq current.getString("code") }.empty() }) {
                log().info("About to persist a set: ${current.getString("name")}")
                transaction {
                    MtgSetTable.insert {
                        it[name] = current.getString("name")
                        it[code] = current.getString("code")
                        it[releasedDate] = current.getString("released_at")?.let { date -> LocalDate.parse(date, dateFormatter) }
                        it[setType] = SetType.valueOf(current.getString("set_type").uppercase())
                        it[block] = current.takeIf { block -> block.has("block") }?.getString("block")
                        it[blockCode] = current.takeIf { blockCode -> blockCode.has("block_code") }?.getString("block_code")
                        it[cardCount] = current.getInt("card_count")
                    }
                }
            } else {
                log().info("Will not persist ${current.getString("name")}")
            }
        }
    }

    /**
     * Discord allows adding a strip of color to the left side of the embedded message, so match the card's color
     */
    private fun determineColor(identity: MutableList<Any>?): Color {
        return if (identity.isNullOrEmpty()) {
            Color.GRAY
        } else if (identity.size > 1) {
            Color(100, 84, 0)
        } else {
            when (identity.first()) {
                "B" -> Color.BLACK
                "G" -> Color.GREEN
                "R" -> Color.RED
                "U" -> Color.BLUE
                "W" -> Color.WHITE
                else -> Color.GRAY
            }
        }
    }
}