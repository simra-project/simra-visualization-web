package com.simra.app.csvimporter.service

import com.simra.app.csvimporter.controller.LegRepository
import com.simra.app.csvimporter.model.LegEntity
import com.simra.app.csvimporter.model.LegPropertyEntity
import com.simra.app.csvimporter.model.RideEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonLineString
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Component
import kotlin.math.abs


data class LegIntersectionContainer(val leg: LegEntity, val index: Int, val direction: Int);


@Component
class LegPartitioningService(@Autowired val legRepository: LegRepository) {
    /**
     *
     * Punkt für Punkt durch die hinzuzufügende line gehen
     * Checken mit welchen legs der aktuelle Punkt intersected
     * Checken mit welchen legs der nachfolgende Punkt intersected
     * In der Schnittmenge checken ob beide Punkte aufeinander folgen (vorwärts oder rückwärts)
     * Das sollte in 0 oder 1 legs resultieren, nie mehr
     * Wenn 0: Punkt speichern und wieder nach oben
     * Wenn 1:
     * bisherige Punkte in neues Leg speichern
     * Weiter durch hinzuzufügende Line und gleichzeitig das Leg steppen bis nicht mehr gleich
     * Das alte Leg löschen
     * 3 neue legs speichern: altes Leg bis intersection, intersection, rest von altem Leg
     * Und wieder nach oben
     *
     */
    @Synchronized
    fun mergeRideIntoLegs(ride: RideEntity) {

        val rideAsLeg = parseRideToLeg(ride)

        var intersectingLegs = legRepository.findByGeometryIntersection(rideAsLeg)

        if (intersectingLegs.isEmpty()) {
            legRepository.save(rideAsLeg)
            return
        }

        val result = mutableListOf<LegEntity>()

        val pointList = mutableListOf<Point>()
        var i = 0
        while (i in rideAsLeg.geometryForKotlin.coordinates.indices) {
            val coordinate = rideAsLeg.geometryForKotlin.coordinates[i]
            val nextCoordinate: Point?
            try {
                nextCoordinate = rideAsLeg.geometryForKotlin.coordinates[i + 1]
            } catch (e: IndexOutOfBoundsException) {
                pointList.add(coordinate)
                if (pointList.size > 1) {
                    val newLeg = createLeg(pointList, mutableSetOf<String>().apply { add(ride.fileId) })
                    result.add(newLeg)
                }
                break
            }

            val intersectingLegsWithCoordinate = intersectingLegs.filter { it.geometryForKotlin.coordinates.contains(coordinate) }
            val intersectingLegsWithNextCoordinate = intersectingLegs.filter { it.geometryForKotlin.coordinates.contains(nextCoordinate) }

            val intersectingLegsWithBoth = intersectingLegsWithCoordinate.intersect(intersectingLegsWithNextCoordinate)


            val matchingLeg = findLegWithSuccessiveCoordinates(intersectingLegsWithBoth, coordinate, nextCoordinate)

            pointList.add(coordinate)
            if (matchingLeg != null) {

                if (pointList.size > 1) {
                    val newLeg = createLeg(pointList, sortedSetOf(ride.fileId))
                    result.add(newLeg)
                }

                pointList.clear()

                val matchingLegUntilIntersection = copy(matchingLeg.leg)
                val firstIntersectingIndex = matchingLeg.index
                var lastIntersectingIndex: Int? = null

                if ((firstIntersectingIndex > 0 && matchingLeg.direction == 1) || (firstIntersectingIndex < matchingLeg.leg.geometryForKotlin.coordinates.size - 1 && matchingLeg.direction == -1)) {
                    if (matchingLeg.direction == -1) {
                        matchingLegUntilIntersection.geometryForKotlin = GeoJsonLineString(matchingLegUntilIntersection.geometryForKotlin.coordinates.subList(firstIntersectingIndex, matchingLeg.leg.geometryForKotlin.coordinates.size))
                    } else {
                        matchingLegUntilIntersection.geometryForKotlin = GeoJsonLineString(matchingLegUntilIntersection.geometryForKotlin.coordinates.subList(0, firstIntersectingIndex + 1)) // +1 to include coordinate
                    }
                    result.add(matchingLegUntilIntersection)
                }

                var k = firstIntersectingIndex
                var breaked = false;
                for (j in i until rideAsLeg.geometryForKotlin.coordinates.size) {

                    val coordinateRide = rideAsLeg.geometryForKotlin.coordinates[j]
                    val coordinateMatchingLeg: Point?
                    try {
                        coordinateMatchingLeg = matchingLeg.leg.geometryForKotlin.coordinates[k]
                    } catch (e: IndexOutOfBoundsException) {
                        i = j - 2 // to add prev point in next leg
                        val fileIds = mutableSetOf<String>().apply { addAll(matchingLeg.leg.propertiesForKotlin.fileIdSetForKotlin) }
                        fileIds.add(ride.fileId)
                        result.add(createLeg(pointList, fileIds))
                        pointList.clear()
                        breaked = true
                        break
                    }
                    if (coordinateRide == coordinateMatchingLeg) {
                        pointList.add(rideAsLeg.geometryForKotlin.coordinates[j])
                    } else {
                        i = j - 2 // to add prev point in next Leg
                        lastIntersectingIndex = k - matchingLeg.direction
                        val fileIds = mutableSetOf<String>().apply { addAll(matchingLeg.leg.propertiesForKotlin.fileIdSetForKotlin) }
                        fileIds.add(ride.fileId)
                        result.add(createLeg(pointList, fileIds))
                        pointList.clear()
                        breaked = true
                        break
                    }
                    k += matchingLeg.direction
                }
                if (!breaked) {
                    if (pointList.size > 1) {
                        i = rideAsLeg.geometryForKotlin.coordinates.size - 1
                        val fileIds = mutableSetOf<String>().apply { addAll(matchingLeg.leg.propertiesForKotlin.fileIdSetForKotlin) }
                        fileIds.add(ride.fileId)
                        result.add(createLeg(pointList, fileIds))
                        pointList.clear()
                    }
                }
                if (lastIntersectingIndex != null) {
                    val matchingLegFromIntersection = copy(matchingLeg.leg)
                    if ((lastIntersectingIndex > 0 && matchingLeg.direction == -1) || (lastIntersectingIndex < matchingLeg.leg.geometryForKotlin.coordinates.size - 1 && matchingLeg.direction == 1)) {
                        if (matchingLeg.direction == 1) {
                            matchingLegFromIntersection.geometryForKotlin = GeoJsonLineString(matchingLegFromIntersection.geometryForKotlin.coordinates.subList(lastIntersectingIndex, matchingLeg.leg.geometryForKotlin.coordinates.size))
                        } else {
                            matchingLegFromIntersection.geometryForKotlin = GeoJsonLineString(matchingLegFromIntersection.geometryForKotlin.coordinates.subList(0, lastIntersectingIndex + 1)) // +1 to include coordinate
                        }
                        result.add(matchingLegFromIntersection)
                    }

                }
                legRepository.delete(matchingLeg.leg)
                legRepository.saveAll(result)
                intersectingLegs = legRepository.findByGeometryIntersection(rideAsLeg)
                result.clear()
            }
            i++
        }

        legRepository.saveAll(result)

    }

    private fun findLegWithSuccessiveCoordinates(legs: Set<LegEntity>, coordinate: Point, nextCoordinate: Point): LegIntersectionContainer? {
        for (leg in legs) {
            var nextIndex = -1;
            var coordIndex = -1;
            for (index in leg.geometryForKotlin.coordinates.indices) {
                if (leg.geometryForKotlin.coordinates[index] == coordinate)
                    coordIndex = index;
                if (leg.geometryForKotlin.coordinates[index] == nextCoordinate)
                    nextIndex = index
                if (abs(nextIndex - coordIndex) == 1 && nextIndex != -1 && coordIndex != -1) {
                    return LegIntersectionContainer(leg, coordIndex, nextIndex - coordIndex)
                }
            }
        }
        return null
    }

    private fun copy(leg: LegEntity): LegEntity {
        val newLeg = LegEntity()
        newLeg.geometryForKotlin = GeoJsonLineString(leg.geometryForKotlin.coordinates)
        val props = LegPropertyEntity()
        props.fileIdSetForKotlin = mutableSetOf<String>().apply { addAll(leg.propertiesForKotlin.fileIdSetForKotlin) }
        newLeg.propertiesForKotlin = props
        return newLeg
    }

    private fun createLeg(pointList: MutableList<Point>, fileIds: Set<String>): LegEntity {
        val newLeg = LegEntity()
        newLeg.geometryForKotlin = GeoJsonLineString(pointList.toMutableList())
        val props = LegPropertyEntity()
        props.fileIdSetForKotlin = mutableSetOf<String>().apply { addAll(fileIds) }
        newLeg.propertiesForKotlin = props
        return newLeg
    }

    public fun parseRideToLeg(ride: RideEntity): LegEntity {
        val leg = LegEntity()
        leg.geometryForKotlin = GeoJsonLineString(ride.locationMapMatched.coordinates.map { GeoJsonPoint(it.values[0], it.values[1]) })
        leg.propertiesForKotlin = LegPropertyEntity()
        leg.propertiesForKotlin.fileIdSetForKotlin = sortedSetOf(ride.fileId)
        return leg
    }
}