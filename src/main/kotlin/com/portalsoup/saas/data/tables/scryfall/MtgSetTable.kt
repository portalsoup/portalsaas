package com.portalsoup.saas.data.tables.scryfall

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object MtgSetTable: IntIdTable("mtg_sets") {
    val name = varchar("name", 255)
    val code = varchar("code", 10)
    val releasedDate = date("released_date").nullable()
    val setType = enumerationByName<SetType>("set_type", 50)
    val block= varchar("block", 255).nullable()
    val blockCode=  varchar("block_code", 10).nullable()
    val cardCount=  integer("card_count")
}


class MtgSet(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<MtgSet>(MtgSetTable)

    var name by MtgSetTable.name
    var code by MtgSetTable.code
    var releasedDate by MtgSetTable.releasedDate
    var setType by MtgSetTable.setType
    var block by MtgSetTable.block
    var blockCode by MtgSetTable.blockCode
    var cardCount by MtgSetTable.cardCount
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

