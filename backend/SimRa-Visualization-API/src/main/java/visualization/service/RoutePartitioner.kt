package visualization.service

import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonGeometryCollection
import org.springframework.data.mongodb.core.geo.GeoJsonLineString
import org.springframework.data.mongodb.core.geo.GeoJsonMultiLineString
import visualization.web.resources.RideResource

class RoutePartitioner {

    fun partitionRoutes(rides: List<RideResource>): GeoJsonGeometryCollection? {

        val sharedCoordinates = rides.flatMap { ride -> ride.coordinatesForKotlin.zipWithNext() }.groupingBy { it }.eachCount().filter { it.value > 1 }

        // reverse partitions in order to simplify working on it
        val sharedCoordinatesByCount = mutableMapOf<Int, Set<Point>>().also { res ->
            sharedCoordinates.values.forEach { count ->
                res[count] = sharedCoordinates.filter { it.value == count }.keys.flatMap { setOf(it.first, it.second) }.toSet()
            }
        }.toMap()

        // Coords that need to be removed from the rides.
        val toBeRemovedCoords = sharedCoordinatesByCount.map { it.value.drop(1).dropLast(1) }


        val result: MutableList<GeoJsonMultiLineString> = mutableListOf()
        rides.forEach { ride ->
            val legs = mutableListOf<List<Point>>()
            toBeRemovedCoords.forEach { toBeRemovedCoords ->
                var leg = mutableListOf<Point>()
                for (i in ride.coordinatesForKotlin.indices) {
                    val point = ride.coordinatesForKotlin[i]
                    if (!toBeRemovedCoords.contains(point)) {
                        leg.add(point)
                    } else {
                        legs.add(leg.toList())
                        leg = mutableListOf()
                    }
                }
                legs.add(leg.toList())
                result.add(parseRideToGeoJson(legs))
                // todo add weight
            }
        }

        sharedCoordinatesByCount.entries.forEach {
            val legs = mutableListOf<List<Point>>()
            it.value.forEach { point ->


            }

        }





        return null
    }

    private fun parseRideToGeoJson(legs: MutableList<List<Point>>): GeoJsonMultiLineString {

        val geoJsonLineStrings = legs.map { GeoJsonLineString(it) }
        val res = GeoJsonMultiLineString(geoJsonLineStrings)

        return res
    }


    class GeoJsonFeature {

    }
}