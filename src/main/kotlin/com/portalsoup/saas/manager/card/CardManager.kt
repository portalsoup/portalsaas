package com.portalsoup.saas.manager.card

import kotlinx.coroutines.Deferred
import org.json.JSONObject

abstract class CardManager {
    abstract suspend fun getCardImage(term: String): String
    internal abstract suspend fun getRawCardAsync(term: String): Deferred<JSONObject>
    internal abstract fun getImageUriFromJson(json: JSONObject): String
}
