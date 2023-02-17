package com.portalsoup.saas.data.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.time.LocalDate

object HelloWorldTable : IntIdTable("hello_world") {
    val name = varchar("name", 50)
    val createdOn = date("created_on")
}

data class HelloWorld(
    val id: Int,
    val name: String,
    val createdOn: LocalDate
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = HelloWorld(
            id = resultRow[HelloWorldTable.id].value,
            name = resultRow[HelloWorldTable.name],
            createdOn = resultRow[HelloWorldTable.createdOn]
        )
    }
}