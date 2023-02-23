package com.portalsoup.saas.manager

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.BenchmarkResult
import com.portalsoup.saas.core.Logging
import com.portalsoup.saas.core.log
import com.portalsoup.saas.core.measureDuration
import com.portalsoup.saas.data.tables.pricecharting.VideoGame
import com.portalsoup.saas.data.tables.pricecharting.VideoGamePriceTable
import com.portalsoup.saas.data.tables.pricecharting.VideoGameTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class PriceChartingManager(val httpClient: HttpClient = HttpClient(CIO)): KoinComponent, Logging {

    private val appConfig by inject<AppConfig>()

    private val gamesPersisted = AtomicInteger(0)
    private val gamePricesPersisted = AtomicInteger(0)
    private val gamesProcessed = AtomicInteger(0)
    private val gamesFound = AtomicInteger(0)

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
                            println("Received ${tmpFile.length()} bytes from ${response.contentLength()}")
                        }
                    }
                }
            }
            tmpFile.bufferedReader().useLines { seq ->
                println("Entered the buffered reader")
                seq
                    .filterIndexed { i, _ -> i > 0 } // Remove the header row
                    .forEach(::parseCsvRow)
            }
        }
        log().info("Import stats: duration=[${durationResult.duration}] found=[${gamesFound.get()}] processed=[${gamesProcessed.get()}] added=[${gamesPersisted.get()}] new-price=[${gamePricesPersisted.get()}]")

    }

    private fun parseCsvRow(line: String) {
        val now = LocalDate.now()
        val split = line.split(",")
        gamesProcessed.getAndIncrement()
        println("Found a line ${split.joinToString("     ")}")
        val container = VideoGameContainer(
            id = split[0].toInt(),
            console = split[1],
            product = split[2],
            price = split.getOrNull(3)
        )

        val maybeVideoGame: VideoGame? = transaction {
            val resultRow = VideoGameTable.select { VideoGameTable.priceChartingId eq container.id }.singleOrNull()
            resultRow
                ?.let { VideoGame.fromRow(it) }
        }

        if (maybeVideoGame == null) {
            log().info("Found a new game $container")
            gamesPersisted.incrementAndGet()
            transaction {
                VideoGameTable.insert {
                    it[priceChartingId] = container.id
                    it[consoleName] = container.console
                    it[productName] = container.product
                    it[createdOn] = now
                    it[updatedOn] = now
                }
            }
        }

        transaction {
            gamePricesPersisted.incrementAndGet()
            VideoGamePriceTable.insert {
                it[videoGameId] = container.id
                it[loosePrice] = container.price
                it[createdOn] = now
            }
        }
    }
}

data class VideoGameContainer(val id: Int, val console: String, val product: String, val price: String?)