package com.portalsoup.saas.manager

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.Logging
import com.portalsoup.saas.core.log
import com.portalsoup.saas.data.tables.pricecharting.VideoGame
import com.portalsoup.saas.data.tables.pricecharting.VideoGamePriceTable
import com.portalsoup.saas.data.tables.pricecharting.VideoGameTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class PriceChartingManager(val httpClient: HttpClient = HttpClient(CIO)): KoinComponent, Logging {

    val appConfig by inject<AppConfig>()

    fun updateLoosePriceGuide() {
        val now = LocalDate.now()
        val url = "https://www.pricecharting.com/price-guide/download-custom?t=${appConfig.pricechartingToken}"
        runBlocking {
            httpClient.prepareGet(url).execute { response ->
                val channel: ByteReadChannel = response.bodyAsChannel()

                var isFirstRow = true
                while (!channel.isClosedForRead) {
                    val nextLine = channel.readUTF8Line()
                    if (isFirstRow) {
                        isFirstRow = false
                        continue
                    }
                    val split = nextLine?.split(",") ?: throw RuntimeException("Couldn't split line")
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
                        VideoGamePriceTable.insert {
                            it[videoGameId] = container.id
                            it[loosePrice] = container.price
                            it[createdOn] = now
                        }
                    }
                }
            }
        }
    }
}

data class VideoGameContainer(val id: Int, val console: String, val product: String, val price: String?)