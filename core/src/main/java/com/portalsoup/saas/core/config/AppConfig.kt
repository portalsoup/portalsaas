package com.portalsoup.saas.core.config

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    val jdbc: Jdbc,
    val discord: Discord,
    val strava: Strava,
    val openAI: OpenAI,
    val pricecharting: Pricecharting
) {
    companion object {
        const val PROPS_PATH = "/application.json"
    }

    @Serializable
    data class Jdbc(
        val url: String,
        val driver: String,
        val username: String,
        val password: String,
        val maxPool: Int
    )

    @Serializable
    data class Strava(
        val token: String,
        val athleteId: String
    )

    @Serializable
    data class Discord(
        val token: String?,
        val user: DiscordUser,
        val guild: DiscordGuild
    ) {
        @Serializable
        data class DiscordUser(
            val id: String
        )

        @Serializable
        data class DiscordGuild(
            val id: String,
            val vipId: String
        )
    }

    @Serializable
    data class OpenAI(
        val token: String
    )

    @Serializable
    data class Pricecharting(
        val token: String
    )
}