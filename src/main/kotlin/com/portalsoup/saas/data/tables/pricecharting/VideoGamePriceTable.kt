package com.portalsoup.saas.data.tables.pricecharting

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object VideoGamePriceTable : IntIdTable("video_game_price") {
    val videoGameId = integer("video_game_id").references(VideoGameTable.priceChartingId)
    val loosePrice = varchar("loose_price", 50).nullable()
    val createdOn = date("created_on")
}