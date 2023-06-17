package com.portalsoup.saas.core.web

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Api: KoinComponent, Logging {

    private val client by inject<HttpClient>()


    suspend fun get(url: String, queryHeaders: Map<String, String> = mapOf()): HttpResponse {
        return client.get(url) {
            headers { queryHeaders.onEach { append(it.key, it.value) } }
        }
    }

    suspend fun makeRequest(url: String, queryHeaders: Map<String, String> = mapOf()): String {
        val response: HttpResponse = client.get(url) {
            headers { queryHeaders.onEach { append(it.key, it.value) } }
        }

        if (response.status.value in 200..299) {
            log().debug("Returning response:\n\n$response\n\n")
            return response.body()
        } else {
            throw NoResultsFoundException()
        }
    }
    class NoResultsFoundException: RuntimeException()
}
