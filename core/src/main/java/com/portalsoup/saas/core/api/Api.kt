package com.portalsoup.saas.core.api

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

abstract class Api: Logging {

    internal val client by lazy {
        HttpClient(CIO)
    }


    suspend fun get(url: String, queryHeaders: Map<String, String> = mapOf()): HttpResponse {
        return client.get(url) {
            headers { queryHeaders.onEach { append(it.key, it.value) } }
        }
    }

    suspend fun makeRequest(url: String, queryHeaders: Map<String, String> = mapOf()): String {
        println("Making request...")
        val response: HttpResponse = client.get(url) {
            headers { queryHeaders.onEach { append(it.key, it.value) } }
        }

        println("Got a response! $response")

        if (response.status.value in 200..299) {
            log().debug("Returning response:\n\n$response\n\n")
            val body: String =  response.body()
            println(body)
            return body
        } else {
            println("Didn't get a response... ${response.status.value}")
            throw com.portalsoup.saas.core.api.Api.NoResultsFoundException()
        }
    }
    class NoResultsFoundException: RuntimeException()
}
