package com.portalsoup.saas.core

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object Api: KoinComponent {

    private val client by inject<HttpClient>()

    private val log = getLogger(javaClass)

    suspend fun makeRequest(url: String, queryHeaders: Map<String, String> = mapOf()): String {
        val response: HttpResponse = client.get(url) {
            headers { queryHeaders.onEach { append(it.key, it.value) } }
        }

        if (response.status.value in 200..299) {
            log.debug("Returning response:\n\n$response\n\n")
            return response.body()
        } else {
            throw NoResultsFoundException()
        }
    }
    class NoResultsFoundException: RuntimeException()
}


fun JSONObject.safeGetString(str: String): String? {
    return if (has(str)) {
        getString(str)
    } else {
        null
    }
}

fun JSONObject.safeGetInt(int: String): Int? {
    return if (has(int)) {
        return try {
            getString(int).toInt()
        } catch (e: RuntimeException) {
            null
        }
    } else {
        null
    }
}