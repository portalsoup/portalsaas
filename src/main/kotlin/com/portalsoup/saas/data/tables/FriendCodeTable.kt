package com.portalsoup.saas.data.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object FriendCodeTable: IntIdTable("friend_code") {
    val user = varchar("userid", 50)
    val code = varchar("code", 50)
    val createdOn = date("created_on")
}

data class FriendCode(
    val id: Int,
    val user: String,
    val code: String,
    val createdOn: LocalDate
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = FriendCode(
            id = resultRow[FriendCodeTable.id].value,
            user = resultRow[FriendCodeTable.user],
            code = resultRow[FriendCodeTable.code],
            createdOn = resultRow[FriendCodeTable.createdOn]
        )
    }
}