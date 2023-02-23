package com.portalsoup.saas.manager.card

import com.portalsoup.saas.core.*
import com.portalsoup.saas.core.extensions.*
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONObject

class MagicManager: CardManager(), Logging {

    private val url = "https://api.scryfall.com/cards/named?fuzzy="
    private val errorText = "Oops, something went wrong"

    private val noResultsText = "Didn't find the card.  The search could have been too broad to confidently" +
            " pick the correct match, or didn't match any cards at all."

    override suspend fun getRawCardAsync(term: String): Deferred<JSONObject> = coroutineScope {
        async { JSONObject(Api.makeRequest(url + term)) }
    }

    override fun getImageUriFromJson(json: JSONObject): String =
        when {
            json.has("image_uris") -> {
                json
                    .getJSONObject("image_uris")
                    .getString("normal").toString()
            }
            json.has("details") -> {
                json
                    .getString("details")
            }
            else -> {
                throw RuntimeException()
            }
        }

    suspend fun embed(term: String): EmbedCreateSpec {
        val card = getRawCardAsync(term).await()

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

    override suspend fun getCardImage(term: String): String {
        val result = kotlin.runCatching {
            val json = getRawCardAsync(term)
            getImageUriFromJson(json.await())
        }

        return if (result.isSuccess) {
            return result.getOrThrow()
        } else {
            when (result.exceptionOrNull()) {
                is Api.NoResultsFoundException -> noResultsText
                else -> errorText
            }
        }
    }
}