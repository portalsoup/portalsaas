package com.portalsoup.saas.config

object JdbcConfig {
    val jdbcUrl: String = System.getenv("JDBC_URL")
    val driverClassName: String = System.getenv("JDBC_DRIVER")
    val username: String = System.getenv("JDBC_USERNAME")
    val password: String = System.getenv("JDBC_PASSWORD")
    val maximumPoolSize: Int = System.getenv("JDBC_MAX_POOL").toInt()
    val connectionTestQuery: String = "SELECT 1"
}
