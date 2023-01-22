package com.portalsoup.saas.manager

import com.portalsoup.saas.config.PriceChartingConfig
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import java.io.File

class PriceChartingManager {

    private val priceGuideFile = "price-guide"
    private val priceGuideSuffix = "csv"

    fun requestCsv() {
        val client = HttpClient()
        val tmpFile = File.createTempFile(priceGuideFile, priceGuideSuffix)
        println("about to request csv")


        runBlocking {
            println("in thread")
            client.prepareGet(PRICE_GUIDE_CSV).execute { response: HttpResponse ->
                println("parsing response")
                val channel = response.bodyAsChannel()

                print("Reading bytes")
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    while (!packet.isEmpty) {
                        val bytes = packet.readBytes()
                        tmpFile.appendBytes(bytes)
                        print(".")
                    }
                }
                println("done")
            }
        }

        copyBackup(tmpFile)
    }

    fun copyBackup(file: File) {
        println("Copying file")
        val backupCopy = file.copyTo(File("${PriceChartingConfig.priceBackupFilePath}/$priceGuideFile.$priceGuideSuffix"), false)
        println("Backed up to ${backupCopy.absolutePath}")
    }
    companion object {
        private val PRICE_GUIDE_CSV = "https://www.pricecharting.com/price-guide/download-custom?t=${PriceChartingConfig.apiKey}"
    }
}

object PriceChartingProductQuery {
    private val endpoint = "https://www.pricecharting.com/api/product?t=${PriceChartingConfig.apiKey}"
    fun byId(id: Int): String = "$endpoint&id=$id"
    fun byQuery(query: String): String = "$endpoint&q=$query"
    fun byUPC(upc: String): String = "$endpoint&upc=$upc"
}