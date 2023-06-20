package com.portalsoup.saas.service

import com.portalsoup.saas.core.extensions.Logging
import io.jenetics.jpx.GPX
import org.openstreetmap.gui.jmapviewer.Coordinate
import org.openstreetmap.gui.jmapviewer.JMapViewerTree
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl
import org.openstreetmap.gui.jmapviewer.OsmTileLoader
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource.Mapnik
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel


fun <R> Optional<R>.orNull(): R? = this.orElse(null)
class OpenMapsManager: Logging {
    val dimensions = Dimension(500, 500)

    val mapTree: JMapViewerTree = JMapViewerTree("routes").apply {
        size = dimensions
        viewer.size = dimensions
        viewer.setTileLoader(OsmTileLoader(viewer))
        viewer.setTileSource(Mapnik())
        viewer.setMapMarkerVisible(true)
        viewer.setZoomContolsVisible(true)

        viewer.setDisplayToFitMapPolygons()
        setTreeVisible(true)
    }

    fun renderMap(gpx: GPX? = null): BufferedImage {

        val defaultStartingPosition = Coordinate(0.0, 0.0)

            mapTree.apply {
                gpx?.also { viewer.setDisplayToFitMapPolygons() } ?: viewer.setDisplayPosition(defaultStartingPosition, 4)
                gpx?.let { generatePolygons(it) }?.map { viewer.addMapPolygon(it) }
            }

        val panel = JPanel()
        val bufferedImage = BufferedImage(dimensions.width, dimensions.height, TYPE_INT_RGB)

        panel.size = dimensions
        panel.add(mapTree)
        panel.revalidate()

        Thread.sleep(5000)

        panel.paint(bufferedImage.graphics)

        return bufferedImage
    }

    fun generatePolygons(gpx: GPX): List<MapPolygon> {
        gpx.tracks.map { track ->
            track.segments.map { it.points }.map { points ->
                for (i in 0..points.size) {
                    if (i == 0) {
                        continue
                    }
                    MapPolygonImpl(
                        Coordinate(points[i-1].latitude.toDouble(), points[i-1].longitude.toDouble()),
                        Coordinate(points[i].latitude.toDouble(), points[i].longitude.toDouble())
                    )
                }
            }
        }

        return listOf()
    }

}

class OsmMapViewer : JFrame("JMapViewer Demo"), JMapViewerEventListener {
    private val treeMap: JMapViewerTree = OpenMapsManager().mapTree
    /**
     * Setups the JFrame layout, sets some default options for the JMapViewerTree and displays a map in the window.
     */
    init {
        add(treeMap, BorderLayout.CENTER)
        pack()
    }

    override fun processCommand(command: JMVCommandEvent) {
        // ...
    }

    companion object {
        private const val serialVersionUID = 1L

        /**
         * @param args Main program arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            OsmMapViewer().isVisible = true
        }
    }
}