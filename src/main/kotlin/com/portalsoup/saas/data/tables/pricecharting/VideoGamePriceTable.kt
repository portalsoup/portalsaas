package com.portalsoup.saas.data.tables.pricecharting

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.ResultRow
import java.sql.ResultSet
import java.time.LocalDate


object VideoGamePriceTable : IntIdTable("video_game_price") {
    val videoGameId = integer("video_game_id").references(VideoGameTable.priceChartingId)
    val loosePrice = varchar("loose_price", 50).nullable()
    val createdOn = date("created_on")
}

@Suppress("unused")
data class VideoGamePrice(
    val id: Int?,
    val videoGameId: Int,
    val loosePrice: String?,
    val createdOn: LocalDate
) {
    companion object {

        @Suppress("unused")
        fun fromRow(resultRow: ResultRow) = VideoGamePrice(
            id = resultRow[VideoGamePriceTable.id].value,
            videoGameId = resultRow[VideoGamePriceTable.videoGameId],
            loosePrice = resultRow[VideoGamePriceTable.loosePrice],
            createdOn = resultRow[VideoGamePriceTable.createdOn]
        )

        @Suppress("unused")
        fun fromSet(resultSet: ResultSet): VideoGamePrice = VideoGamePrice(
            id = runCatching { resultSet.getInt("_id") }.getOrNull(),
            videoGameId = resultSet.getInt("video_game_id"),
            loosePrice = kotlin.runCatching { resultSet.getString("loose_price") }.getOrNull(),
            createdOn = resultSet.getDate("created_on").toLocalDate()

        )
    }
}