package com.portalsoup.saas.manager

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import java.io.File

class PriceChartingManager {


    fun requestCsv() {
        val client = HttpClient()
        val tmpFile = File.createTempFile("price-guide", "csv")
        println("about to request csv")


        runBlocking {
            println("in thread")
            client.prepareGet("https://google.com").execute {
                println("got google response")
            }
            client.prepareGet(ENDPOINT).execute { response: HttpResponse ->
                println("parsing response")
                val channel = response.bodyAsChannel()

                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    while (!packet.isEmpty) {
                        val bytes = packet.readBytes()
                        tmpFile.appendBytes(bytes)
                        println("Received ${bytes.size} bytes")
                    }
                }
            }
        }
    }

    companion object {
        private const val ENDPOINT = "https://www.pricecharting.com/price-guide/download-custom?t=6f02aae4e844b99b20bb7fff71fe68c2123b5f33"
    }
}