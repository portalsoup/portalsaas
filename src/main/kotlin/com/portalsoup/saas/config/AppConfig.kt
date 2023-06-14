package com.portalsoup.saas.config

import io.ktor.server.application.*
import org.koin.core.component.KoinComponent

data class AppConfig(
    val jdbcConfig: Jdbc,
    val strava: Strava,
    val myDiscordID: String,
    val discordToken: String?,
    val pricechartingToken: String?,
    val openaiToken: String?,
) : KoinComponent {
    companion object {
        fun default(environment: ApplicationEnvironment): AppConfig {
            val jdbcUrl = System.getenv("JDBC_URL") ?: environment.config.property("jdbc.url").getString()
            val jdbcDriver = System.getenv("JDBC_DRIVER") ?: environment.config.property("jdbc.driver").getString()
            val jdbcUsername = System.getenv("JDBC_USERNAME") ?: environment.config.property("jdbc.username").getString()
            val jdbcPassword = System.getenv("JDBC_PASSWORD") ?: environment.config.property("jdbc.password").getString()
            val jdbcMaxPool = System.getenv("JDBC_MAX_POOL")?.toInt() ?: environment.config.property("jdbc.maxPool").getString().toInt()
            val myDiscordID = System.getenv("DISCORD_USER_ID") ?: environment.config.property("discord.myUserID").getString()
            val discordToken = System.getenv("DISCORD_TOKEN") ?: environment.config.property("discord.token").getString()
            val pricechartingToken = System.getenv("PRICECHARTING_TOKEN") ?: environment.config.property("pricecharting.token").getString()
            val openaiToken = System.getenv("OPENAI_TOKEN") ?: environment.config.property("openai.token").getString()
            val stravaToken = System.getenv("STRAVA_TOKEN") ?: environment.config.property("strava.token").getString()
            val stravaAthleteID = System.getenv("STRAVA_ATHLETE_ID") ?: environment.config.property("strava.athleteID").getString()

            return AppConfig(
                Jdbc(
                    url = jdbcUrl,
                    driver = jdbcDriver,
                    username = jdbcUsername,
                    password = jdbcPassword,
                    maxPool = jdbcMaxPool
                ),
                Strava(stravaToken, stravaAthleteID),
                discordToken = discordToken,
                pricechartingToken = pricechartingToken,
                openaiToken = openaiToken,
                myDiscordID = myDiscordID
            )
        }
    }
}

data class Jdbc(
    val url: String,
    val driver: String,
    val username: String,
    val password: String,
    val maxPool: Int
)

data class Strava(
    val token: String,
    val athleteID: String
)