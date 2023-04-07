package com.portalsoup.saas.discord

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import discord4j.common.JacksonResources
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import java.io.InputStream
import java.net.URI

class CommandReader(val client: RestClient): Logging {

    fun init(commandDefinitions: List<String>) {
        val mapper = JacksonResources.create()

        val commands = commandDefinitions
            .map { getResourceAsString(it) }
            .map { mapper.objectMapper.readValue(it, ApplicationCommandRequest::class.java) }

        client.applicationId.block()?.let { id ->
            client.applicationService.bulkOverwriteGlobalApplicationCommand(id, commands)
                .doOnNext { log().debug("Global command [${it.name()}] registered") }
                .doOnError { log().error("Failed to register global commands", it) }
                .subscribe()
        }
    }

    private fun getResourceAsString(fileName: String): InputStream? = ClassLoader
        .getSystemClassLoader()
        .getResourceAsStream(URI(DISCORD_COMMAND_JSON_LOCATION).resolve("$fileName.json").toString())

    companion object {
        private const val DISCORD_COMMAND_JSON_LOCATION = "discord/commands/"
    }
}