package com.portalsoup.saas.api.card

import kotlinx.coroutines.Deferred
import org.json.JSONObject

abstract class CardApi {
    abstract suspend fun getCardImage(term: String): String
    internal abstract suspend fun getRawCardAsync(term: String): Deferred<JSONObject>
    internal abstract fun getImageUriFromJson(json: JSONObject): String
}
