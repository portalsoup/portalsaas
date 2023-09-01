package com.portalsoup.saas.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object HelloWorldTable : IntIdTable("hello_world") {
    val name = varchar("name", 50)
    val createdOn = date("created_on")
}

class HelloWorld(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<HelloWorld>(HelloWorldTable)

    var name by HelloWorldTable.name
    var createdOn by HelloWorldTable.createdOn
}