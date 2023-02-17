package com.portalsoup.saas.api.card

import com.portalsoup.saas.core.Api
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONObject

class MagicApi: CardApi() {

    val url = "https://api.scryfall.com/cards/named?fuzzy="
    val errorText = "Oops, something went wrong"

    val noResultsText = "Didn't find the card.  The search could have been too broad to confidently" +
            " pick the correct match, or didn't match any cards at all."

    override suspend fun getRawCardAsync(term: String): Deferred<JSONObject> = coroutineScope {
        async { JSONObject(Api.makeRequest(url + term)).also { println(it) } }
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

        println(card.toString(4))

        val color = determineColor(kotlin.runCatching { card.getJSONArray("color_identity") }.getOrNull()?.toList())
        val cardName = kotlin.runCatching { card.getString("name") }.getOrNull() ?: ""
        val uri = kotlin.runCatching { card.getString("scryfall_uri") }.getOrNull() ?: ""
        val costLabel = "Mana Cost"
        val cost = kotlin.runCatching { card.getString("mana_cost") }.getOrNull() ?: ""
        val oracleText = kotlin.runCatching { card.getString("oracle_text") }.getOrNull() ?: ""
        val flavorText = kotlin.runCatching { card.getString("flavor_text") }.getOrNull() ?: ""
        val cardImage = kotlin.runCatching { card.getJSONObject("image_uris").getString("large") }.getOrNull() ?: ""
        val artCrop = kotlin.runCatching { card.getJSONObject("image_uris").getString("art_crop") }.getOrNull() ?: ""
        val atkLabel = kotlin.runCatching { card.getString("Power") }.getOrNull() ?: ""
        val atk = kotlin.runCatching { card.getString("power") }.getOrNull() ?: ""
        val defLabel = kotlin.runCatching { card.getString("Toughness") }.getOrNull() ?: ""
        val def = kotlin.runCatching { card.getString("toughness") }.getOrNull() ?: ""
        val spellType = kotlin.runCatching { card.getString("type_line") }.getOrNull() ?: ""


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

    fun determineColor(identity: MutableList<Any>?): Color {
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