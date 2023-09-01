package com.portalsoup.saas.config


object AppConfig {
    val discord: Discord = readDiscord()
    val jdbc: Jdbc = readJdbc()
    val strava: Strava = readStrava()

    val pricechartingToken = System.getenv("PRICECHARTING_TOKEN") ?: throw IllegalStateException("No Pricecharting token found.")
    val openaiToken = System.getenv("OPENAI_TOKEN") ?: throw IllegalStateException("No Open AI token found.")


    private fun readDiscord(): Discord {
        val userId = System.getenv("DISCORD_USER_ID") ?: throw IllegalStateException("No Discord user ID found.")
        val token = System.getenv("DISCORD_TOKEN") ?: throw IllegalStateException("No Discord token found.")
        val guildId = System.getenv("DISCORD_GUILD_ID") ?: throw IllegalStateException("No Discord guild ID found.")
        val guildVipId = System.getenv("DISCORD_VIP_GUILD_ID") ?: throw IllegalStateException("No Discord vip guild ID found.")

        return Discord(token, userId, guildId, guildVipId)
    }

    private fun readJdbc(): Jdbc {
        val jdbcUrl = System.getenv("JDBC_URL") ?: throw IllegalStateException("No JDBC URL found.")
        val jdbcDriver = System.getenv("JDBC_DRIVER") ?: throw IllegalStateException("No No JDBC driver found.")
        val jdbcUsername = System.getenv("JDBC_USERNAME") ?: throw IllegalStateException("No JDBC username found.")
        val jdbcPassword = System.getenv("JDBC_PASSWORD") ?: throw IllegalStateException("No JDBC password found.")
        val jdbcMaxPool = System.getenv("JDBC_MAX_POOL")?.toInt() ?: throw IllegalStateException("No JDBC max pool found.")

        return Jdbc(jdbcUrl, jdbcDriver, jdbcUsername, jdbcPassword, jdbcMaxPool)

    }

    private fun readStrava(): Strava {
        val token = System.getenv("STRAVA_TOKEN") ?: throw IllegalStateException("No Strava token found.")
        val athleteID = System.getenv("STRAVA_ATHLETE_ID") ?: throw IllegalStateException("No Strava athlete ID found.")

        return Strava(token, athleteID)
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