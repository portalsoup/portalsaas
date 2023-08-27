package com.portalsoup.saas.data.dao

import com.portalsoup.saas.core.db.execAndMap
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

object PriceChartingDAO {

    data class PriceResult(val pricechartingId: String, val name: String, val console: String, val price: String?, val priceDate: LocalDate)


    fun lookupGamePrice(game: String, maxResults: Int): List<PriceResult> = transaction {
        // Raw query is required here because of the trigrams extension
        """
            |SELECT
            |  vg.pricecharting_id,
            |  vg.product_name || vg.console_name <-> '$game' as dist,
            |  vg.product_name,
            |  vg.console_name,
            |  loose_price,
            |  video_game_price.created_on as price_date
            |FROM
            |  video_game as vg
            |  LEFT OUTER JOIN video_game_price ON video_game_price.video_game_id = vg.pricecharting_id
            |ORDER by dist, price_date LIMIT $maxResults;
        """.trimMargin().execAndMap {
            PriceResult(
                name = it.getString("product_name"),
                console = it.getString("console_name"),
                price = it.getString("loose_price"),
                pricechartingId = it.getString("pricecharting_id"),
                priceDate = it.getDate("price_date").toLocalDate()
            )
        }
    }
}