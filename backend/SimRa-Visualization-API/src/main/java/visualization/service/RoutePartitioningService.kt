package visualization.service

import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonLineString
import org.springframework.stereotype.Component
import visualization.web.resources.LegResource
import visualization.web.resources.LegResourceProperty
import visualization.web.resources.RideResource

@Component
class RoutePartitioningService {

    fun partitionRoutes(rides: List<RideResource>): ArrayList<LegResource> {

        val partitionedRides: MutableMap<Int, List<List<Point>>> = mutableMapOf()

        // find ride segments which are used more than once
        val sharedCoordinates = rides.flatMap { ride -> ride.geometryForKotlin.coordinates.zipWithNext() }.groupingBy { it }.eachCount().filter { it.value > 1 }

        // Restructure Map in order to Count be Key
        val sharedCoordinatesPreprocessed = mutableMapOf<Int, List<Pair<Point, Point>>>().also { res ->
            sharedCoordinates.values.forEach { count ->
                res[count] = sharedCoordinates.filter { it.value == count }.keys.map { it }
            }
        }.toMap()


        val distinctSharedLegs = findDistinctLegs(sharedCoordinatesPreprocessed)

        val flatSharedLegs = distinctSharedLegs.keys.flatten()

        partitionedRides[1] = findOnceUsedRideSegments(rides, flatSharedLegs)

        // add Route Legs that have been used more often than once
        partitionedRides.also { result ->
            distinctSharedLegs.values.forEach { count ->
                result[count] = distinctSharedLegs.filter { it.value == count }.keys.flatMap { listOf(it) }
            }
        }

        val result = arrayListOf<LegResource>()

        partitionedRides.forEach { count ->
            val legResourceProperty = LegResourceProperty()
            legResourceProperty.setWeightForKotlin(count.key)

            count.value.forEach { points ->
                val legResource = LegResource()
                if (points.size > 1) {
                    legResource.setGeometryForKotlin(GeoJsonLineString(points))
                    legResource.setPropertiesKotlin(legResourceProperty)
                    result.add(legResource)
                }
            }
        }

        return result
    }

    private fun findOnceUsedRideSegments(rides: List<RideResource>, flatSharedLegs: List<Point>): List<List<Point>> {
        val onceUsedRideLegs = mutableListOf<List<Point>>()
        rides.forEach { ride ->
            var rideLeg = mutableListOf<Point>()
            for (i in ride.geometryForKotlin.coordinates.indices) {
                try {
                    val curPoint = ride.geometryForKotlin.coordinates[i]
                    val prevPoint = ride.geometryForKotlin.coordinates.getOrNull(i - 1)
                    if (flatSharedLegs.contains(curPoint)) {
                        if (!flatSharedLegs.contains(prevPoint)) {
                            rideLeg.add(curPoint)
                        }
                        if (rideLeg.isNotEmpty()) {
                            onceUsedRideLegs.add(rideLeg.toList())
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
            if (rideLeg.isNotEmpty()) onceUsedRideLegs.add(rideLeg)
        }
        return onceUsedRideLegs
    }

    private fun findDistinctLegs(sharedCoordinates: Map<Int, List<Pair<Point, Point>>>): Map<List<Point>, Int> {

        val result = mutableMapOf<List<Point>, Int>()

        sharedCoordinates.forEach {
            var leg = mutableListOf<Point>()
            for (i in it.value.indices) {
                leg.add(it.value[i].first)
                leg.add(it.value[i].second)
                if ((i + 1 < it.value.size && it.value[i].second != it.value[i + 1].first) || i + 1 == it.value.size) {
                    result[leg.distinct().toList()] = it.key
                    leg = mutableListOf()
                }
            }
        }

        return result
    }
}