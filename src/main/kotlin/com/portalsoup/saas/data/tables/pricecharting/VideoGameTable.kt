package com.portalsoup.saas.data.tables.pricecharting

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.sql.ResultSet
import java.time.LocalDate

object VideoGameTable : IntIdTable("video_game") {
    val priceChartingId = integer("pricecharting_id").uniqueIndex()
    val consoleName = varchar("console_name", 50)
    val productName = varchar("product_name", 250)
    val createdOn = date("created_on")
    val updatedOn = date("updated_on")
}

data class VideoGame(
    val id: Int,
    val pricechartingId: Int,
    val consoleName: String,
    val productName: String,
    val createdOn: LocalDate,
    val updatedOn: LocalDate
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = VideoGame(
            id = resultRow[VideoGameTable.id].value,
            pricechartingId = resultRow[VideoGameTable.priceChartingId],
            consoleName = resultRow[VideoGameTable.consoleName],
            productName = resultRow[VideoGameTable.productName],
            createdOn = resultRow[VideoGameTable.createdOn],
            updatedOn = resultRow[VideoGameTable.updatedOn]
        )

        @Suppress("unused")
        fun fromSet(resultSet: ResultSet): VideoGame = VideoGame(
            id = resultSet.getInt("id"),
            pricechartingId = resultSet.getInt("pricecharting_id"),
            consoleName = resultSet.getString("console_name"),
            productName = resultSet.getString("product_name"),
            createdOn = resultSet.getDate("created_on").toLocalDate(),
            updatedOn = resultSet.getDate("updated_on").toLocalDate()
        )
    }
}
