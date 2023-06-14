package com.portalsoup.saas.manager

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.web.Api
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StravaManager: Logging, KoinComponent {

    val baseUrl = "https://www.strava.com/api/v3"

    val appConfig by inject<AppConfig>()

    private suspend fun get(endpoint: String): JSONObject? = coroutineScope {
        async { runCatching { JSONObject(Api.makeRequest("$baseUrl$endpoint")) }.getOrNull() }.await()
    }

    fun listRoutesAPI() {
        val response: JSONObject? = runBlocking {
            get("/athletes/${appConfig.strava.athleteID}/routes")
        }
    }
}