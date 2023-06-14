package com.portalsoup.saas.data.tables.gpx

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object CoordinateTable : IntIdTable("coordinate") {
    val lat = float("lat")
    val lng = float("lng")
    val altitude = float("altitude").nullable()
    val route = reference("route_id", RouteTable)
    val created = date("created_date")
    val heartRate = integer("heart_rate").nullable()
}

class Coordinate(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<Coordinate>(CoordinateTable)

    var lat by CoordinateTable.lat
    var lng by CoordinateTable.lng
    var altitude by CoordinateTable.altitude
    var route by Route referencedOn CoordinateTable.route
}