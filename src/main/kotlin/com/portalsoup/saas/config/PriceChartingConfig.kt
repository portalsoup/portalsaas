package com.portalsoup.saas.config

object PriceChartingConfig {
    val priceBackupFilePath: String = System.getenv("PRICE_CHART_BACKUP_PATH")
    val apiKey: String = System.getenv("PRICE_CHART_KEY")
}
