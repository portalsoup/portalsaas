package com.portalsoup.saas.discord.command.pokedex

import com.portalsoup.saas.discord.command.Command
import com.portalsoup.saas.manager.PokemonManager
import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

object PokedexCommand: Command {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val term = event.message.content.split("!pokedex").lastOrNull() ?: return Mono.empty()
        val isShiny = term.contains("shiny")
        val name = term.replace("shiny", "").trim()

        return event.message.channel
            .flatMap { it.createMessage(PokemonManager.getPokemonByName(name, isShiny)) }
            .then()
    }
}