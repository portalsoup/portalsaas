package com.portalsoup.saas.common.pokedex

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PokemonSpecies(
    @SerialName("id")
    val pokedexId: Int,

    val name: String,

    @SerialName("egg_groups")
    val eggGroups: List<EggGroup>,

    @SerialName("flavor_text_entries")
    val flavorTexts: List<FlavorText>,

    @SerialName("hatch_counter")
    private val hatchCounter: Int,

    @SerialName("gender_rate")
    private val genderRate: Int,
) {
    val stepsToHatch = hatchCounter
        get() = field * 256

    val femaleChance: Int = genderRate
        get() = ((field.toDouble() / 8) * 100).toInt()
}

@Serializable
data class EggGroup(
    val name: String
)

@Serializable
data class FlavorText(
    @SerialName("flavor_text")
    val text: String,
    val language: FlavorTextLanguage
) {
    @Serializable
    data class FlavorTextLanguage(val name: String)
}