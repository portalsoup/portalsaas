package com.portalsoup.saas.service

import com.portalsoup.saas.db.tables.gpx.Coordinate
import com.portalsoup.saas.db.tables.gpx.Route
import io.jenetics.jpx.GPX
import io.jenetics.jpx.TrackSegment
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.LocalDate
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object GPXManager {

    fun importGpxFile(file: File) = file
        .let { GPX.read(it.toPath()) }
        .let { importGpx(it) }

    fun importGpx(gpx: GPX): List<Route> = gpx.tracks().toList()
        .map { track ->
            val i = AtomicInteger(0)
            val uuid = UUID.randomUUID().toString()
            val getName = track.name.orElseGet { "${uuid}_$i" }
            transaction {
                println("About to create route: \n\t${getName}")
                val routeCreated = Route.new { name = getName }
                track.segments
                    .also { println("About to persist a list of ${it.size} coordinates") }
                    .map { mapAndPersistPoints(routeCreated, it) }
                return@transaction routeCreated
            }
        }

    fun mapAndPersistPoints(route: Route, trackSegment: TrackSegment): List<Coordinate> {
        return transaction {
            trackSegment.points.map {
                Coordinate.new {
                    lat = it.latitude.toFloat()
                    lng = it.longitude.toFloat()
                    altitude = it.elevation.map { it.toFloat() }.orElse(null)
                    this.route = route
                    created = LocalDate.now()
                    heartRate = null
                }
            }
        }
    }
}