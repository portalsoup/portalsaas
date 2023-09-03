package com.portalsoup.saas.common.pokedex

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ability(
    @SerialName("effect_entries")
    val effectEntries: List<EffectEntry>
) {
    @Serializable
    data class EffectEntry(
        val effect: String,
        @SerialName("short_effect")
        val shortEffect: String,
        val language: Language
    )

    @Serializable
    data class Language(
        val name: String
    )
}