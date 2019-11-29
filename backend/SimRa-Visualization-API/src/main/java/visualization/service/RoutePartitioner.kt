package visualization.service

import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonGeometryCollection
import org.springframework.data.mongodb.core.geo.GeoJsonLineString
import org.springframework.data.mongodb.core.geo.GeoJsonMultiLineString
import visualization.web.resources.RideResource

class RoutePartitioner {

    fun partitionRoutes(rides: List<RideResource>): GeoJsonGeometryCollection? {

        val sharedCoordinates = rides.flatMap { ride -> ride.coordinatesForKotlin.zipWithNext() }.groupingBy { it }.eachCount().filter { it.value > 1 }

        val sharedCoordinatesPreprocessed = mutableMapOf<Int, List<Pair<Point, Point>>>().also { res ->
            sharedCoordinates.values.forEach { count ->
                res[count] = sharedCoordinates.filter { it.value == count }.keys.map { it }
            }
        }.toMap()


        // reverse partitions in order to simplify working on it
        val sharedCoordinatesByCount = mutableMapOf<Int, Set<Point>>().also { res ->
            sharedCoordinates.values.forEach { count ->
                res[count] = sharedCoordinates.filter { it.value == count }.keys.flatMap { setOf(it.first, it.second) }.toSet()
            }
        }.toMap()

        val distinctSharedLags = findDistinctLags(sharedCoordinatesPreprocessed)!!


        val flatSharedLegs = distinctSharedLags.flatten()


        val result: MutableMap<List<List<Point>>, Int> = mutableMapOf()
        rides.forEach { ride ->
            val rideLegs = mutableListOf<List<Point>>()
            var rideLeg = mutableListOf<Point>()
            for (i in ride.coordinatesForKotlin.indices) {
                try {
                    val curPoint = ride.coordinatesForKotlin[i]
                    val prevPoint = ride.coordinatesForKotlin.getOrNull(i - 1)
                    if (flatSharedLegs.contains(curPoint)) {
                        if (!flatSharedLegs.contains(prevPoint)) {
                            rideLeg.add(curPoint)
                        }
                        if (rideLeg.isNotEmpty()) {
                            rideLegs.add(rideLeg.toList())
                            rideLeg = mutableListOf()
                        }
                    } else {
                        if (flatSharedLegs.contains(prevPoint)) {
                            rideLeg.add(prevPoint!!)
                        }
                        rideLeg.add(curPoint)
                    }
                } catch (e: IndexOutOfBoundsException) {
                    continue
                }

            }
            if (rideLeg.isNotEmpty()) rideLegs.add(rideLeg)
            result[rideLegs] = 1
        }



        return null
    }

    private fun findDistinctLags(sharedCoordinates: Map<Int, List<Pair<Point, Point>>>): Set<List<Point>>? {

        val result = hashSetOf<List<Point>>()

        sharedCoordinates.forEach {
            var leg = mutableListOf<Point>()
            for (i in it.value.indices) {
                leg.add(it.value[i].first)
                leg.add(it.value[i].second)
                if (i + 1 < it.value.size && it.value[i].second != it.value[i + 1].first || i + 1 == it.value.size) {
                    result.add(leg.distinct().toList())
                    leg = mutableListOf()
                }
            }
        }

        return result
    }

    private fun parseRideToGeoJson(legs: MutableList<List<Point>>): GeoJsonMultiLineString {

        val geoJsonLineStrings = legs.map { GeoJsonLineString(it) }
        val res = GeoJsonMultiLineString(geoJsonLineStrings)

        return res
    }


    class GeoJsonFeature {

    }
}