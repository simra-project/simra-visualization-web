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


@Component
class LegPartitioningService(@Autowired val legRepository: LegRepository) {

    fun mergeRideIntoLegs(ride: RideEntity): List<LegEntity> {

        val rideLeg = parseRideToLeg(ride)

        val legIntersectEntities = legRepository.findByGeometryIntersection(rideLeg)

        val result: MutableList<LegEntity> = mutableListOf()

        for (intersectingLeg in legIntersectEntities) {


            val subLegs = findSubLegs(rideLeg, intersectingLeg).toMutableList()
            subLegs.addAll(findSubLegs(intersectingLeg, rideLeg))

            val test = subLegs.toSet()
            val a = 12
        }


        return result.toList()
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
                subLeg.geometryForKotlin = GeoJsonLineString(pointList.distinct())
                result.add(subLeg)
                continue
            }

            pointList.add(coordinate)
            if (addPreviousPoint) {
                pointList.add(toBeSlicedLeg.geometryForKotlin.coordinates[i - 1])
                addPreviousPoint = false
            }

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
                if (pointList.size < 2) { // intersections can only intersect at one point. Ignore then
                    pointList.clear()
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