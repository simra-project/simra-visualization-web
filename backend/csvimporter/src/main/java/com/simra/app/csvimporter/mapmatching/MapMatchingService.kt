package main.java.com.simra.app.csvimporter.mapmatching

import com.graphhopper.PathWrapper
import com.graphhopper.matching.MapMatching
import com.graphhopper.matching.Observation
import com.graphhopper.reader.osm.GraphHopperOSM
import com.graphhopper.routing.AlgorithmOptions
import com.graphhopper.routing.util.HintsMap
import com.graphhopper.routing.util.TraversalMode
import com.graphhopper.routing.weighting.FastestWeighting
import com.graphhopper.util.*
import com.graphhopper.util.shapes.GHPoint
import com.graphhopper.util.shapes.GHPoint3D
import io.jenetics.jpx.GPX
import main.java.com.simra.app.csvimporter.model.RideCSV
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapMatchingService {

    var currentRouteDistance: Float = 0F;

    /**
     * Snaps the GPS Coordinates onto OSM-Streets.
     * TODO Add possibility to snap Routes in other cities than Berlin
     */
    fun matchToMap(rideBeans: List<RideCSV>): List<RideCSV> {

        val gpx = GPX.builder()
                .addTrack { track ->
                    track.addSegment { segment ->
                        rideBeans.forEach {
                            segment.addPoint { p -> p.lat(it.lat.toDouble()).lon(it.lon.toDouble()) }
                        }
                    }
                }.build()


        val graphHopperConfiguration = CmdArgs()
        graphHopperConfiguration.put("graph.flag_encoders", "bike")
        graphHopperConfiguration.put("datareader.file", "backend/csvimporter/map-data/Brandenburg_and_Berlin.osm.pbf")

        val graphHopper = GraphHopperOSM().init(graphHopperConfiguration)
        graphHopper.importOrLoad()

        val firstEncoder = graphHopper.encodingManager.fetchEdgeEncoders()[0]
        val opts = AlgorithmOptions.start()
                .algorithm(Parameters.Algorithms.DIJKSTRA_BI)
                .traversalMode(TraversalMode.EDGE_BASED)
                .weighting(FastestWeighting(firstEncoder))
                .maxVisitedNodes(2000)
                .hints(HintsMap().put("weighting", "fastest")
                        .put("vehicle", "bike"))
                // Penalizing inner-link U-turns only works with fastest weighting, since
                // shortest weighting does not apply penalties to unfavored virtual edges.
                .build()

        val mapMatching = MapMatching(graphHopper, HintsMap(opts.hints))
        mapMatching.setTransitionProbabilityBeta(2.0)
        mapMatching.setMeasurementErrorSigma(50.0)

        val matchSW = StopWatch()

        val tr = TranslationMap().doImport().getWithFallBack(Locale.GERMANY)

        val measurements = gpx.tracks[0].segments.flatMap { segment -> segment.map { Observation(GHPoint(it.latitude.toDouble(), it.longitude.toDouble())) } }
        matchSW.start()
        val mr = mapMatching.doWork(measurements)
        matchSW.stop()
        println("\tmatches:\t" + mr.edgeMatches.size + ", gps entries:" + measurements.size)


        val pathWrapper = PathWrapper()
        PathMerger(graphHopper.graphHopperStorage, opts.weighting).doWork(pathWrapper, listOf(mr.mergedPath), graphHopper.encodingManager, tr)

        // Copy metainfo of closest (Raw) Point to Snapped Point
        val result = pathWrapper.points.map {
            val rideCSV = RideCSV()

            rideCSV.lat = it.lat.toString()
            rideCSV.lon = it.lon.toString()

            val nextPoint = findNearestPoint(rideBeans, it)
            rideCSV.a = nextPoint.a
            rideCSV.b = nextPoint.b
            rideCSV.c = nextPoint.c
            rideCSV.acc = nextPoint.acc
            rideCSV.timeStamp = nextPoint.timeStamp
            rideCSV.x = nextPoint.x
            rideCSV.y = nextPoint.y
            rideCSV.z = nextPoint.z

            rideCSV
        }

        currentRouteDistance = pathWrapper.distance.toFloat()

        println("matching took: " + matchSW.seconds)

        return result
    }

    private fun findNearestPoint(rawRideCSVList: List<RideCSV>, matchedPoint: GHPoint3D): RideCSV {
        var nextPoint: RideCSV? = null
        var minDist: Double? = null
        rawRideCSVList.forEach {
            val dist = distance(it.lat.toDouble(), it.lon.toDouble(), matchedPoint.lat, matchedPoint.lon)
            if (minDist == null || dist < minDist!!) {
                minDist = dist
                nextPoint = it
            }
        }
        return nextPoint!!
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius of the earth

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = sin(latDistance / 2) * sin(latDistance / 2) + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R.toDouble() * c * 1000.0
    }
}