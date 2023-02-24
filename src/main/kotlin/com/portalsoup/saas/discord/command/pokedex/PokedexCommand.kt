package com.portalsoup.saas.discord.command.pokedex

import com.portalsoup.saas.discord.command.IDiscordCommand
import com.portalsoup.saas.manager.PokemonManager
import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

object PokedexCommand: IDiscordCommand {
    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val term = event.message.content.split("!pokedex").lastOrNull() ?: return Mono.empty()
        val isShiny = term.contains("shiny")
        val name = term.replace("shiny", "").trim()

        val foundPokemon = PokemonManager.getPokemonByName(name, isShiny)
            ?: return event.message.channel
                .flatMap { it.createMessage("I couldn't find a pokemon that matched") }
                .then()

        return event.message.channel
            .flatMap { it.createMessage(foundPokemon) }
            .then()
    }
}