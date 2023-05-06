package com.portalsoup.saas.manager

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.core.web.Api
import com.portalsoup.saas.dto.pokedex.Ability
import com.portalsoup.saas.dto.pokedex.Pokemon
import com.portalsoup.saas.dto.pokedex.PokemonHandle
import com.portalsoup.saas.dto.pokedex.PokemonSpecies
import discord4j.core.spec.EmbedCreateSpec
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Collect functionality to interact with pokeapi's API
 */
class PokemonManager: Logging {
    companion object {
        private const val host = "https://pokeapi.co/api/v2/"
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    /**
     * Retrieve a Pokemon's pokedex entry by name.
     *
     * @return A Discord embed message containing the requested Pokemon's details or null if no
     *   matching Pokemon is found
     */
    fun getPokemonByName(pkmnName: String, shiny: Boolean): EmbedCreateSpec? {
        val pokemonHandle = getPokemon(pkmnName)

        if (pokemonHandle.pokemon == null || pokemonHandle.pokemonSpecies == null) {
            return null
        }

        val pokemon = pokemonHandle.pokemon
        val species = pokemonHandle.pokemonSpecies

        val abilitiesText = pokemon.abilities.map { ability ->
            "**${ability.name.name}**${ability.shortText?.let { " - $it" } ?: ""}"
        }

        val hp = pokemon.stats.first { it.stat.name == "hp" }.base
        val atk = pokemon.stats.first { it.stat.name == "attack" }.base
        val def = pokemon.stats.first { it.stat.name == "defense" }.base
        val spAtk = pokemon.stats.first { it.stat.name == "special-attack" }.base
        val spDef = pokemon.stats.first { it.stat.name == "special-defense" }.base
        val spd = pokemon.stats.first { it.stat.name == "speed" }.base

        val stats = "**HP**: $hp **Atk**: $atk **Sp Atk**: $spAtk" +
                "\n**Spd**: $spd **Def**: $def  **Sp Def**: $spDef"

        val types = pokemon.types.joinToString(", ") { type -> type.name.name }

        val frontSprite = shiny
            .takeIf { it }
            ?.let { pokemon.sprites.shinyFront }
            ?: pokemon.sprites.front

        val backSprite = shiny
            .takeIf { it }
            ?.let { pokemon.sprites.shinyBack }
            ?: pokemon.sprites.back

        val frontHighQualitySprite = shiny
            .takeIf { it }
            ?.let { pokemon.sprites.other?.home?.shinyFront }
            ?: pokemon.sprites.other?.home?.front

        // return
        return EmbedCreateSpec.builder()
            .title("${pokemon.name} (# ${pokemon.pokedexId}")
            .description(species.flavorTexts.first().text)
            .addField(
                "Base stats",
                stats,
                true
            )
            .addField(
                "Types",
                types,
                false
            )
            .addField(
                "EV Points",
                pokemon.stats.filter { it.effort > 0 }.joinToString("\n") { "${it.effort} ${it.stat.name}" },
                true
            )
            .addField(
                "Chance of female",
                "${species.femaleChance}%",
                true
            )
            .addField(
                "Steps to hatch",
                species.stepsToHatch.toString(),
                true
            )
            .addField(
                "Abilities",
                abilitiesText.joinToString("\n\n"),
                false
            )
            .image(frontHighQualitySprite ?: frontSprite)
            .thumbnail(backSprite)
            .build()
    }

    /**
     * Fetch a Pokemon's details by name
     *
     * @return The found Pokemon or null
     */
    private fun pokemonEndpoint(name: String): Pokemon? {
        log().info("Found the name [$name]")
        val response = runBlocking {
            runCatching { Api.makeRequest("${Companion.host}/pokemon/$name") }.getOrNull()
        }
        val pokemon = response?.let { json.decodeFromString<Pokemon>(response) }
        return pokemon?.copy(
            wildHoldItems = pokemon.wildHoldItems.map {
                it.copy(
                    item = it.item,
                    frequency = listOf(it.frequency.last())
                )
            }
        )
    }

    /**
     * Fetch a Pokemon's species details by name
     *
     * @return The found PokemonSpecies or null
     */
    private fun pokemonSpeciesEndpoint(name: String): PokemonSpecies? {
        val response = runBlocking {
            runCatching { Api.makeRequest("${Companion.host}/pokemon-species/$name") }.getOrNull()
        }
        val species = response?.let { json.decodeFromString<PokemonSpecies>(response) }
        return species?.copy(
            flavorTexts = listOf(species.flavorTexts
                .last { it.language.name == "en" }
                .let { it.copy(text = it.text.replace("[^\\x00-\\x7F]".toRegex(), "")) }
            )
        )
    }

    /**
     * Fetch a Pokemon's ability details by name
     *
     * @return The found Ability or null
     */
    private fun pokemonAbilityEndpoint(url: String): Ability? {
        val response = runBlocking { runCatching { Api.makeRequest(url) }.getOrNull() }
        val ability = response?.let { json.decodeFromString<Ability>(response) }
        return ability?.copy(
            effectEntries = listOf(
                ability.effectEntries.last { it.language.name == "en" }
            )
        )
    }

    private fun getPokemon(name: String): PokemonHandle {
        val pokemon = pokemonEndpoint(name)
        val pokemonSpecies = pokemonSpeciesEndpoint(name)
        val abilities = pokemon?.abilities?.map { ability ->
            ability.copy(shortText = pokemonAbilityEndpoint(ability.name.url)
                ?.effectEntries
                ?.last { it.language.name == "en" }
                ?.shortEffect
            )
        }

        return PokemonHandle(
            pokemon?.copy(abilities = abilities ?: emptyList()),
            pokemonSpecies
        )
    }
}