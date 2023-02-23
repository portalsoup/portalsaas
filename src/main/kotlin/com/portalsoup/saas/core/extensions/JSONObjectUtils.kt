package com.portalsoup.saas.core.extensions

import org.json.JSONArray
import org.json.JSONObject


fun JSONObject.safeGetString(str: String): String? {
    return if (has(str)) {
        runCatching { getString(str) }.getOrNull()
    } else {
        null
    }
}

@Suppress("unused")
fun JSONObject.safeGetInt(int: String): Int? {
    return if (has(int)) {
        runCatching { getString(int).toInt() }.getOrNull()
    } else {
        null
    }
}

fun JSONObject.safeGetJSONObject(obj: String): JSONObject? {
    return if (has(obj)) {
        runCatching { getJSONObject(obj) }.getOrNull()
    } else {
        null
    }
}

fun JSONObject.safeGetJsonArray(str: String): JSONArray? {
    return if (has(str)) {
        runCatching { getJSONArray(str) }.getOrNull()
    } else {
        null
    }
}