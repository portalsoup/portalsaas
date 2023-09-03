package com.portalsoup.saas.core.db.tables.gpx

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object RouteTable : IntIdTable("route") {
    val name = varchar("name", 255)
}

class Route(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Route>(RouteTable)

    var name by RouteTable.name

    var blogPosts by BlogPost via BlogPostRouteTable
}