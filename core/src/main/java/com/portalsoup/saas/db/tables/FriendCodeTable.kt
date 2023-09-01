package com.portalsoup.saas.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object FriendCodeTable: IntIdTable("friend_code") {
    val user = varchar("userid", 50)
    val code = varchar("code", 50)
    val createdOn = date("created_on")
}

class FriendCode(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FriendCode>(FriendCodeTable)

    var user by FriendCodeTable.user
    var code by FriendCodeTable.code
    var createdOn by FriendCodeTable.createdOn
}