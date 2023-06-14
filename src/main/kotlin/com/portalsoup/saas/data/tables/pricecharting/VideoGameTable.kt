package com.portalsoup.saas.data.tables.pricecharting

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object VideoGameTable : IntIdTable("video_game") {
    val priceChartingId = integer("pricecharting_id").uniqueIndex()
    val consoleName = varchar("console_name", 50)
    val productName = varchar("product_name", 250)
    val createdOn = date("created_on")
    val updatedOn = date("updated_on")
}

class VideoGame(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<VideoGame>(VideoGameTable)

    var priceChartingId by VideoGameTable.priceChartingId
    var consoleName by VideoGameTable.consoleName
    var productName by VideoGameTable.productName
    var createdOn by VideoGameTable.createdOn
    var updatedOn by VideoGameTable.updatedOn
}