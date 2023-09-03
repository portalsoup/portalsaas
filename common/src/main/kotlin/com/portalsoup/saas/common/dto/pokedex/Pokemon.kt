package com.portalsoup.saas.common.pokedex

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Pokemon(
    @SerialName("id")
    val pokedexId: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val types: List<Type>,
    val stats: List<Stat>,
    val abilities: List<PokemonAbility>,
    @SerialName("held_items")
    val wildHoldItems: List<WildHoldItem>,
)

@Serializable
data class Sprites(
    @SerialName("front_default")
    val front: String,

    @SerialName("back_default")
    val back: String,

    @SerialName("front_shiny")
    val shinyFront: String,

    @SerialName("back_shiny")
    val shinyBack: String,

    val other: Other?,
) {
    @Serializable
    data class Other(
        val home: Home?
    )

    @Serializable
    data class Home(
        @SerialName("front_default")
        val front: String?,
        @SerialName("front_shiny")
        val shinyFront: String?
    )
}

@Serializable
data class Type(
    @SerialName("type")
    val name: TypeName
) {

    companion object {
        fun create(name: String) = Type(name = TypeName(name))
    }

    @Serializable
    data class TypeName(
        val name: String
    )
}

@Serializable
data class Stat(
    @SerialName("base_stat")
    val base: Int,

    val effort: Int,

    @SerialName("stat")
    val stat: StatName,
) {

    companion object {
        fun create(
            base: Int,
            effort: Int,
            statName: String
        ) = Stat(base, effort, StatName(statName))
    }

    @Serializable
    data class StatName(
        val name: String
    )
}

@Serializable
data class PokemonAbility(
    @SerialName("is_hidden")
    val hidden: Boolean,

    @SerialName("ability")
    val name: AbilityName,

    val shortText: String?
) {
    companion object {
        fun create(
            hidden: Boolean,
            name: String,
            url: String,
            shortText: String?
        ) = PokemonAbility(hidden, AbilityName(name, url), shortText = shortText)
    }

    @Serializable
    data class AbilityName(
        val name: String,
        val url: String
    )
}

@Serializable
data class WildHoldItem(
    val item: Item,

    @SerialName("version_details")
    val frequency: List<ItemFrequency>
) {
    @Serializable
    data class Item(
        val name: String
    )

    @Serializable
    data class ItemFrequency(
        val rarity: Int,
    )
}