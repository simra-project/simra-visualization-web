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

    fun mergeRideIntoLegs(ride: RideEntity) {

        val rideAsLeg = parseRideToLeg(ride)

        val legsOnNewRide = mutableSetOf(rideAsLeg)

        var legIntersectEntities = legRepository.findByGeometryIntersection(rideAsLeg)


        if (legIntersectEntities.isEmpty()) {
            legRepository.save(rideAsLeg)
            return
        }
        val subLegs: MutableSet<LegEntity> = mutableSetOf()
        for (intersectingLeg in legIntersectEntities) {

            for (legOnNewRide in legsOnNewRide) {
                subLegs.addAll(findSubLegs(legOnNewRide, intersectingLeg))
                subLegs.addAll(findSubLegs(intersectingLeg, legOnNewRide))
            }

            legsOnNewRide.removeAll(legsOnNewRide)
            legsOnNewRide.addAll(subLegs)
        }

        legRepository.deleteAll(legIntersectEntities)
        legRepository.saveAll(subLegs)
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