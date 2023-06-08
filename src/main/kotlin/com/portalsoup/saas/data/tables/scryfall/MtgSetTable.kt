package com.portalsoup.saas.data.tables.scryfall

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object MtgSetTable: IntIdTable("mtg_sets") {
    val name = varchar("name", 255)
    val code = varchar("code", 10)
    val releasedDate = date("released_date").nullable()
    val setType = enumerationByName<SetType>("set_type", 50)
    val block= varchar("block", 255).nullable()
    val blockCode=  varchar("block_code", 10).nullable()
    val cardCount=  integer("card_count")
}

data class MtgSet(
    val id: Int,
    val name: String, // English name of the set
    val code: String, // Unique 3-5 letter code for this set
    val releasedDate: LocalDate?,
    val setType: SetType,
    val block: String?,
    val blockCode: String?,
    val cardCount: Int
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = MtgSet(
            id = resultRow[MtgSetTable.id].value,
            name = resultRow[MtgSetTable.name],
            code = resultRow[MtgSetTable.code],
            releasedDate = resultRow[MtgSetTable.releasedDate],
            setType = resultRow[MtgSetTable.setType],
            block = resultRow[MtgSetTable.block],
            blockCode = resultRow[MtgSetTable.blockCode],
            cardCount = resultRow[MtgSetTable.cardCount]
        )
    }
}

enum class SetType {
    CORE,
    EXPANSION,
    MASTERS,
    ALCHEMY,
    MASTERPIECE,
    ARSENAL,
    FROM_THE_VAULT,
    SPELLBOOK,
    PREMIUM_DECK,
    DUEL_DECK,
    DRAFT_INNOVATION,
    TREASURE_CHEST,
    COMMANDER,
    PLANECHASE,
    ARCHENEMY,
    VANGUARD,
    FUNNY,
    STARTER,
    BOX,
    PROMO,
    TOKEN,
    MEMORABILIA,
    MINIGAME
}

