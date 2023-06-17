package com.portalsoup.saas.config

import io.ktor.server.application.*
import org.koin.core.component.KoinComponent

data class AppConfig(
    val discord: Discord,
    val jdbcConfig: Jdbc,
    val strava: Strava,
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
            
            val myDiscordID = environment.config.property("discord.userID").getString()
            val discordToken = environment.config.property("discord.token").getString()
            val discordGuildID = environment.config.property("discord.guildID").getString()
            val discordGuildVIPID = environment.config.property("discord.guildVIPChannelID").getString()
            
            val pricechartingToken = environment.config.property("pricecharting.token").getString()
            val openaiToken = environment.config.property("openai.token").getString()
            val stravaToken = environment.config.property("strava.token").getString()
            val stravaAthleteID = environment.config.property("strava.athleteID").getString()

            return AppConfig(
                discord = Discord(
                    token = discordToken,
                    userID = myDiscordID,
                    guildID = discordGuildID,
                    guildVIPChannelID = discordGuildVIPID
                ),
                jdbcConfig = Jdbc(
                    url = jdbcUrl,
                    driver = jdbcDriver,
                    username = jdbcUsername,
                    password = jdbcPassword,
                    maxPool = jdbcMaxPool
                ),
                strava = Strava(stravaToken, stravaAthleteID),
                pricechartingToken = pricechartingToken,
                openaiToken = openaiToken,
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

data class Discord(
    val token: String?,
    val userID: String,
    val guildID: String,
    val guildVIPChannelID: String
)