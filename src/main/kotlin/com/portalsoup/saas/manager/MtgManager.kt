package com.portalsoup.saas.manager

import com.portalsoup.saas.core.extensions.*
import com.portalsoup.saas.core.web.Api
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONObject

/**
 * Collect functionality to interact with scryfall's API
 */
class MtgManager: Logging {

    private val url = "https://api.scryfall.com/cards/named?fuzzy="

    /**
     * Search for a card by name and return a Discord embedded message object containing the found card's
     */
    suspend fun embed(term: String): EmbedCreateSpec? {
        val card = getRawCardAsync(term).await() ?: return null

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


        return EmbedCreateSpec.builder()
            .color(color)
            .author(cardName, uri, null)
            .thumbnail(cardImage)
            .addField("", spellType, true)
            .addField("", oracleText, false)
            .addField(costLabel, cost, true)
            .addField(
                listOf(atkLabel, defLabel).filter { it.isNotEmpty() }.joinToString("/"),
                listOf(atk, def).filter { it.isNotEmpty() }.joinToString("/"),
                true
            )
            .image(artCrop)
            .footer(flavorText, null)
            .build()
    }

    /**
     * Fetch a card by name from scryfall's API.  Scryfall supports fuzzy searching
     */
    private suspend fun getRawCardAsync(term: String): Deferred<JSONObject?> = coroutineScope {
        async { runCatching { JSONObject(Api.makeRequest(url + term)) }.getOrNull() }
    }


    /**
     * Discord allows adding a strip of color to the left side of the embedded message, so match the card's color
     */
    private fun determineColor(identity: MutableList<Any>?): Color {
        return if (identity.isNullOrEmpty()) {
            Color.GRAY
        } else if (identity.size > 1) {
            Color.TAHITI_GOLD
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