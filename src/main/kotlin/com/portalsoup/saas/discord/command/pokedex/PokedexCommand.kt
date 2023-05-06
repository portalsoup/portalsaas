package com.portalsoup.saas.discord.command.pokedex

import com.portalsoup.saas.discord.command.IDiscordCommand
import com.portalsoup.saas.manager.PokemonManager
import discord4j.core.event.domain.message.MessageCreateEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import reactor.core.publisher.Mono

/**
 * Look up a Pokemon's game details by name using pokeapi.
 */
object PokedexCommand: IDiscordCommand, KoinComponent {

    val pokemonManager: PokemonManager by inject()

    override fun execute(event: MessageCreateEvent, truncatedMessage: String): Mono<Void> {
        val term = event.message.content.split("!pokedex").lastOrNull() ?: return Mono.empty()
        val isShiny = term.contains("shiny")
        val name = term.replace("shiny", "").trim()

        val foundPokemon = pokemonManager.getPokemonByName(name, isShiny)
            ?: return event.message.channel
                .flatMap { it.createMessage("I couldn't find a pokemon that matched") }
                .then()

        return event.message.channel
            .flatMap { it.createMessage(foundPokemon) }
            .then()
    }
}