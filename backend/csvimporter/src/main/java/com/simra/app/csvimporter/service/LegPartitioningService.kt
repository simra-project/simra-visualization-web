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


    fun mergeRideIntoLegs(ride: RideEntity) {

        val rideAsLeg = parseRideToLeg(ride)

        val intersectingLegs = legRepository.findByGeometryIntersection(rideAsLeg)

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
                val newLeg = createLeg(pointList, mutableSetOf<String>().apply { add(ride.fileId) })
                result.add(newLeg)
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

                val matchingLegUntilIntersection = copy(matchingLeg)
                val firstIntersectingIndex = matchingLegUntilIntersection.geometryForKotlin.coordinates.indexOf(coordinate)
                var lastIntersectingIndex: Int? = null

                if (firstIntersectingIndex > 0) {
                    matchingLegUntilIntersection.geometryForKotlin = GeoJsonLineString(matchingLegUntilIntersection.geometryForKotlin.coordinates.subList(0, firstIntersectingIndex + 1)) // +1 to include coordinate
                    result.add(matchingLegUntilIntersection)
                }

                var k = firstIntersectingIndex

                for (j in i until rideAsLeg.geometryForKotlin.coordinates.size) {

                    val coordinateRide = rideAsLeg.geometryForKotlin.coordinates[j]
                    val coordinateMatchingLeg: Point?
                    try {
                        coordinateMatchingLeg = matchingLeg.geometryForKotlin.coordinates[k]
                    } catch (e: IndexOutOfBoundsException) {
                        i = j - 2 // to add prev point in next leg
                        val fileIds = mutableSetOf<String>().apply { addAll(matchingLeg.propertiesForKotlin.fileIdSetForKotlin) }
                        fileIds.add(ride.fileId)
                        result.add(createLeg(pointList, fileIds))
                        pointList.clear()
                        break
                    }
                    if (coordinateRide == coordinateMatchingLeg) {
                        pointList.add(rideAsLeg.geometryForKotlin.coordinates[j])
                    } else {
                        i = j - 2 // to add prev point in next Leg
                        lastIntersectingIndex = k - 1
                        val fileIds = mutableSetOf<String>().apply { addAll(matchingLeg.propertiesForKotlin.fileIdSetForKotlin) }
                        fileIds.add(ride.fileId)
                        result.add(createLeg(pointList, fileIds))
                        pointList.clear()
                        break
                    }
                    k++
                }
                if (lastIntersectingIndex != null) {
                    val matchingLegFromIntersection = copy(matchingLeg)
                    if (matchingLeg.geometryForKotlin.coordinates.size - lastIntersectingIndex > 1) {
                        matchingLegFromIntersection.geometryForKotlin = GeoJsonLineString(matchingLegFromIntersection.geometryForKotlin.coordinates.subList(lastIntersectingIndex, matchingLeg.geometryForKotlin.coordinates.size))
                        result.add(matchingLegFromIntersection)
                    }
                }
                legRepository.delete(matchingLeg)
            }
            i++
        }

        legRepository.saveAll(result)

    }

    private fun findLegWithSuccessiveCoordinates(legs: Set<LegEntity>, coordinate: Point, nextCoordinate: Point): LegEntity? {
        return legs.find {
            abs(it.geometryForKotlin.coordinates.indexOf(coordinate) - it.geometryForKotlin.coordinates.indexOf(nextCoordinate)) == 1
        }
    }

    private fun copy(leg: LegEntity): LegEntity {
        val newLeg = LegEntity()
        newLeg.geometryForKotlin = GeoJsonLineString(leg.geometryForKotlin.coordinates)
        val props = LegPropertyEntity()
        props.fileIdSetForKotlin.addAll(leg.propertiesForKotlin.fileIdSetForKotlin)
        newLeg.propertiesForKotlin = props
        return newLeg
    }

    private fun createLeg(pointList: MutableList<Point>, fileIds: Set<String>): LegEntity {
        val newLeg = LegEntity()
        newLeg.geometryForKotlin = GeoJsonLineString(pointList)
        val props = LegPropertyEntity()
        props.fileIdSetForKotlin.addAll(fileIds)
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

    private fun findSubLegs(toBeSlicedLeg: LegEntity, intersectingLeg: LegEntity): List<LegEntity> {

        val result = mutableListOf<LegEntity>()

        val sharedCoordinates = toBeSlicedLeg.geometryForKotlin.coordinates.minus(toBeSlicedLeg.geometryForKotlin.coordinates.minus(intersectingLeg.geometryForKotlin.coordinates)).distinct()

        val pointList = mutableListOf<Point>()

        var addPreviousPoint = false
        for (i in toBeSlicedLeg.geometryForKotlin.coordinates.indices) {
            val coordinate = toBeSlicedLeg.geometryForKotlin.coordinates[i]
            val nextCoordinate: Point?
            try {
                nextCoordinate = toBeSlicedLeg.geometryForKotlin.coordinates[i + 1]
            } catch (e: IndexOutOfBoundsException) {
                pointList.add(coordinate)
                val subLeg = LegEntity()
                subLeg.propertiesForKotlin = toBeSlicedLeg.propertiesForKotlin
                if (intersectingLeg.geometryForKotlin.coordinates.contains(coordinate))
                    subLeg.propertiesForKotlin.fileIdSetForKotlin.addAll(intersectingLeg.propertiesForKotlin.fileIdSetForKotlin)
                if (pointList.size == 1) { // intersections can only intersect at one point. Then merge with prev and next
                    continue
                }
                subLeg.geometryForKotlin = GeoJsonLineString(pointList.distinct())
                result.add(subLeg)
                continue
            }

            if (addPreviousPoint) {
                pointList.add(toBeSlicedLeg.geometryForKotlin.coordinates[i - 1])
                addPreviousPoint = false
            }
            pointList.add(coordinate)

            val transitionBetweenInAndOut = sharedCoordinates.contains(coordinate) && !sharedCoordinates.contains(nextCoordinate)
            val transitionBetweenOutAndIn = !sharedCoordinates.contains(coordinate) && sharedCoordinates.contains(nextCoordinate)
            if (transitionBetweenInAndOut || transitionBetweenOutAndIn) {
                val subLeg = LegEntity()
                val subLegProperty = LegPropertyEntity()
                subLegProperty.fileIdSetForKotlin.addAll(toBeSlicedLeg.propertiesForKotlin.fileIdSetForKotlin)
                if (transitionBetweenInAndOut) { // s s s -> n n s (s=shared, n=not_shared)
                    subLegProperty.fileIdSetForKotlin.addAll(intersectingLeg.propertiesForKotlin.fileIdSetForKotlin)
                    addPreviousPoint = true
                } else { // n n -> s s s s n n
                    pointList.add(nextCoordinate)
                }
                subLeg.propertiesForKotlin = subLegProperty
                if (pointList.size == 1) { // intersections can only intersect at one point. Then merge with prev and next
                    continue
                }
                subLeg.geometryForKotlin = GeoJsonLineString(pointList.distinct())
                result.add(subLeg)
                pointList.clear()
            }
        }
        return result
    }
}