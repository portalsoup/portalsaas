package com.portalsoup.saas.service

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.dto.gpx.Route
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StravaManager: Logging, KoinComponent {

    val baseUrl = "https://www.strava.com/api/v3"

    val appConfig by inject<AppConfig>()
    val client by inject<HttpClient>()

    /*

    $ http get "https://www.strava.com/api/v3/athletes/{id}/routes?page=&per_page=" "Authorization: Bearer [[token]]"


     */

    suspend fun listRoutesAPI(): List<Route> {
        val response: List<Route> = runBlocking {
            client.get("$baseUrl/athletes/${appConfig.strava.athleteID}/routes?client_id=") {
                formData(
                    FormPart("Authorization", "Bearer: ${getAccessToken().accessToken}")
                )
            }
        }.body()

        log().info(response.toString())
        return response
    }

    fun getAccessToken(): OauthTokenResponse = runBlocking {

        val response = client.post("https://www.strava.com/oauth/token") {
            formData(
                FormPart("client_id", "108816"),
                FormPart("client_secret", "e19d2d169dfefcd3507e7d7f8ca51873a2919332"),
//                FormPart("code", token.accessToken),
                FormPart("grant_type", "authorization_code")
            )
        }
//
        return@runBlocking response.body()
    }
}

@Serializable
data class OauthTokenResponse(
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_at") val expiresAt: Long,
    @SerialName("expires_in") val expiresin: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("access_token") val accessToken: String
)

// http://www.strava.com/oauth/authorize?client_id=108816&response_type=code&redirect_uri=http://localhost/exchange_token&approval_prompt=force&scope=read
/**
curl -X POST https://www.strava.com/oauth/token \
-F client_id=108816 \
-F client_secret=e19d2d169dfefcd3507e7d7f8ca51873a2919332 \
-F code=364034eea3c8f587f824172819b703332b3b4083 \
-F grant_type=authorization_code
 */