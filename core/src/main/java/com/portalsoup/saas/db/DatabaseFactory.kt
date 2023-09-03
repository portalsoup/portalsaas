package com.portalsoup.saas.db

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.extensions.Logging
import com.portalsoup.saas.extensions.log
import com.portalsoup.saas.util.Retrier
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import org.jetbrains.exposed.sql.Database

class DatabaseFactory: Logging {

    /**
     * The database's entrypoint, this configures the connection pool with Hikari, and performs necessary migrations
     * using Flyway.
     */
    fun init(appConfig: AppConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = appConfig.jdbc.url
            username = appConfig.jdbc.username
            password = appConfig.jdbc.password
            driverClassName = appConfig.jdbc.driver
            maximumPoolSize = appConfig.jdbc.maxPool
            isAutoCommit = true
            addDataSourceProperty( "cachePrepStmts" , "true" )
            addDataSourceProperty( "prepStmtCacheSize" , "250" )
            addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" )

        }

        hikariConfig.toString()
        val dataSource = Retrier("Create connection pool") {
            HikariDataSource(hikariConfig)
        }

        initFlyway(dataSource)
        initHikari(dataSource)
    }

    private fun initFlyway(dataSource: HikariDataSource) {
        log().info("Initializing flyway...")
        val flyway = Flyway.configure().dataSource(dataSource).load()

        log().info("Performing migration...")
        val result: MigrateResult = flyway.migrate()

        when (result.success) {
            true -> log().info("Migration succeeded...")
            false -> {
                log().info("Migration failed!  Rolling back...")
                flyway.undo()
            }
        }
    }
    private fun initHikari(dataSource: HikariDataSource) {
        log().info("Initializing hikari...")
        Database.connect(dataSource)
    }
}