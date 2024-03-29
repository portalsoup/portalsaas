package com.portalsoup.saas.manager

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.core.util.measureDuration
import com.portalsoup.saas.data.tables.pricecharting.VideoGame
import com.portalsoup.saas.data.tables.pricecharting.VideoGamePriceTable
import com.portalsoup.saas.data.tables.pricecharting.VideoGameTable
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

/**
 * Collect functionality to interact with Pricecharting's API
 */
class PriceChartingManager: KoinComponent, Logging {

    private val appConfig by inject<AppConfig>()
    private val httpClient by inject<HttpClient>()

    private val gamesPersisted = AtomicInteger(0)
    private val gamePricesPersisted = AtomicInteger(0)
    private val gamesProcessed = AtomicInteger(0)
    private val gamesFound = AtomicInteger(0)

    /**
     * Fetch and save the latest video game csv price guide from pricecharting.com
     */
    fun updateLoosePriceGuide() {
        val durationResult = measureDuration {
            val tmpFile = File.createTempFile("pricecharting", Random.nextDouble().toString())
            val url = "https://www.pricecharting.com/price-guide/download-custom?t=${appConfig.pricechartingToken}"
            runBlocking {
                httpClient.prepareGet(url).execute { response ->
                    val channel: ByteReadChannel = response.bodyAsChannel()

                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.isEmpty) {
                            val bytes = packet.readBytes()
                            tmpFile.appendBytes(bytes)
                            log().info("Received ${tmpFile.length()} bytes from ${response.contentLength()}")
                        }
                    }
                }
            }
            tmpFile.bufferedReader().useLines { seq ->
                seq
                    .filterIndexed { i, _ -> i > 0 } // Remove the header row
                    .forEach(::savePriceGuideRow)
            }
        }
        log().info("Import stats: duration=[${durationResult.duration}] found=[${gamesFound.get()}] processed=[${gamesProcessed.get()}] added=[${gamesPersisted.get()}] new-price=[${gamePricesPersisted.get()}]")

    }

    /**
     * Parse and persist a line in the price guide csv
     */
    private fun savePriceGuideRow(line: String) {
        val now = LocalDate.now()
        val split = line.split(",")
        gamesProcessed.getAndIncrement()
        val parsedGame = VideoGameContainer(
            id = split[0].toInt(),
            console = split[1],
            product = split[2],
            price = split.getOrNull(3)
        )

        val maybeVideoGame: VideoGame? = transaction {
            VideoGame.find { VideoGameTable.priceChartingId eq parsedGame.id }.singleOrNull()
        }

        if (maybeVideoGame == null) {
            log().info("Found a new game $parsedGame")
            gamesPersisted.incrementAndGet()
            transaction {
                VideoGameTable.insert {
                    it[priceChartingId] = parsedGame.id
                    it[consoleName] = parsedGame.console
                    it[productName] = parsedGame.product
                    it[createdOn] = now
                    it[updatedOn] = now
                }
            }
        }

        transaction {
            gamePricesPersisted.incrementAndGet()
            VideoGamePriceTable.insert {
                it[videoGame] = parsedGame.id
                it[loosePrice] = parsedGame.price
                it[createdOn] = now
            }
        }
    }
}

/**
 * A container for a single row in the price guide csv
 */
data class VideoGameContainer(val id: Int, val console: String, val product: String, val price: String?)