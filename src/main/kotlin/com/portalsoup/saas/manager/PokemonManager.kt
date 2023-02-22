package com.portalsoup.saas.manager

import com.portalsoup.saas.core.Api
import com.portalsoup.saas.core.Logging
import com.portalsoup.saas.core.log
import com.portalsoup.saas.dto.Ability
import com.portalsoup.saas.dto.Pokemon
import com.portalsoup.saas.dto.PokemonHandle
import com.portalsoup.saas.dto.PokemonSpecies
import discord4j.core.spec.EmbedCreateSpec
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


object PokemonManager: Logging {

    fun getPokemonByName(pkmnName: String, shiny: Boolean): EmbedCreateSpec {
        val pokemonHandle = getPokemon(pkmnName)
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

        val types = pokemon.types.map { type -> type.name.name }.joinToString(", ")

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
                pokemon.stats.filter { it.effort > 0 }.map { "${it.effort} ${it.stat.name}" }.joinToString("\n"),
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

    private val host = "https://pokeapi.co/api/v2/"

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    private fun pokemonEndpoint(name: String): Pokemon {
        log().info("Found the name [$name]")
        val response = runBlocking { Api.makeRequest("${host}/pokemon/$name") }
        val pokemon = json.decodeFromString<Pokemon>(response)
        return pokemon.copy(
            wildHoldItems = pokemon.wildHoldItems.map {
                it.copy(
                    item = it.item,
                    frequency = listOf(it.frequency.last())
                )
            }
        )
    }

    private fun pokemonSpeciesEndpoint(name: String): PokemonSpecies {
        val response = runBlocking { Api.makeRequest("${host}/pokemon-species/$name") }
        println("\n\n$response\n\n")
        val species = json.decodeFromString<PokemonSpecies>(response)
        return species.copy(
            flavorTexts = listOf(species.flavorTexts
                .last { it.language.name == "en" }
                .let { it.copy(text = it.text.replace("[^\\x00-\\x7F]".toRegex(), "")) }
            )
        )
    }

    private fun pokemonAbilityEndpoint(url: String): Ability {
        val response = runBlocking { Api.makeRequest(url) }
        val ability = json.decodeFromString<Ability>(response)
        return ability.copy(
            effectEntries = listOf(
                ability.effectEntries.last { it.language.name == "en" }
            )
        )
    }

    fun getPokemon(name: String): PokemonHandle {
        val pokemon = pokemonEndpoint(name)
        val pokemonSpecies = pokemonSpeciesEndpoint(name)

        return PokemonHandle(
            pokemon.copy(abilities = pokemon.abilities.map {
                it.copy(shortText = pokemonAbilityEndpoint(it.name.url)
                    .effectEntries
                    .last { it.language.name == "en" }
                    .shortEffect
                )
            }),
            pokemonSpecies
        )
    }
}