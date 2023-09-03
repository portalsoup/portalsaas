package com.portalsoup.saas.core.extensions

import org.json.JSONArray
import org.json.JSONObject


/**
 * Safely get a String value from a JSONObject
 *
 * @param str The property name to search
 * @return The found property's value or null
 */
fun JSONObject.safeGetString(str: String): String? {
    return if (has(str)) {
        runCatching { getString(str) }.getOrNull()
    } else {
        null
    }
}

/**
 * Safely get an Int value from a JSONObject
 *
 * @param int The property name to search
 * @return The found property's value or null
 */
@Suppress("unused")
fun JSONObject.safeGetInt(int: String): Int? {
    return if (has(int)) {
        runCatching { getString(int).toInt() }.getOrNull()
    } else {
        null
    }
}

/**
 * Safely get a JSONObject from a JSONObject
 *
 * @param obj The property name to search
 * @return The found property's value or null
 */
fun JSONObject.safeGetJSONObject(obj: String): JSONObject? {
    return if (has(obj)) {
        runCatching { getJSONObject(obj) }.getOrNull()
    } else {
        null
    }
}

/**
 * Safely get a JSONArray from a JSONObject
 *
 * @param arr The property name to search
 * @return The found property's value or null
 */
fun JSONObject.safeGetJsonArray(arr: String): JSONArray? {
    return if (has(arr)) {
        runCatching { getJSONArray(arr) }.getOrNull()
    } else {
        null
    }
}