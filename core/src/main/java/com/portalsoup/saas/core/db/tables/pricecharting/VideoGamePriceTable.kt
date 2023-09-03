package com.portalsoup.saas.core.db.tables.pricecharting

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date


object VideoGamePriceTable : IntIdTable("video_game_price") {
    val videoGame = reference("video_game_id", VideoGameTable)
    val loosePrice = varchar("loose_price", 50).nullable()
    val createdOn = date("created_on")
}


class VideoGamePrice(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<VideoGamePrice>(VideoGamePriceTable)

    var videoGame by VideoGame referencedOn VideoGamePriceTable.videoGame
    var loosePrice by VideoGamePriceTable.loosePrice
    var createdOn by VideoGamePriceTable.createdOn
}