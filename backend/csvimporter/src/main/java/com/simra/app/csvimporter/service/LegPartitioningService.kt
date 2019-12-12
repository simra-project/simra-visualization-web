package com.simra.app.csvimporter.service

import com.simra.app.csvimporter.controller.LegRepository
import com.simra.app.csvimporter.model.LegEntity
import com.simra.app.csvimporter.model.RideEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonLineString
import org.springframework.stereotype.Component


@Component
class LegPartitioningService(@Autowired val legRepository: LegRepository) {

    fun partitionLegs(intersectingLegs: List<LegEntity>, ride: RideEntity): List<LegEntity> {

        val result: MutableList<LegEntity> = mutableListOf()

        for (intersectingLeg in intersectingLegs) {

            val ridePoints = ride.location.coordinates.map { Point(it.values[0], it.values[1]) }

            val sharedCoordinates = intersectingLeg.geometryForKotlin.coordinates.plus(ridePoints).groupingBy { it }.eachCount().filter { it.value == 2 }.keys

            val subLegs = findSubLegs(ridePoints, sharedCoordinates)
            subLegs.forEach {
                it.properties = intersectingLeg.properties
                it.properties.fileIdList.add(ride.fileId)
            }


        }


        return result.toList()
    }

    private fun findSubLegs(points: List<Point>, sharedCoordinates: Set<Point>): List<LegEntity> {

        val result = mutableListOf<LegEntity>()

        var subLeg = LegEntity()
        val pointList = mutableListOf<Point>()
        for (i in points.indices) {
            val coordinate = points[i]
            val nextCoordinate: Point?
            try {
                nextCoordinate = pointList[i + 1]
            } catch (e: IndexOutOfBoundsException) {
                pointList.add(coordinate)
                subLeg.geometryForKotlin = GeoJsonLineString(pointList)
                result.add(subLeg)
                continue
            }
            pointList.add(coordinate)
            if ((sharedCoordinates.contains(coordinate) && !sharedCoordinates.contains(nextCoordinate)) || (!sharedCoordinates.contains(coordinate) && sharedCoordinates.contains(nextCoordinate))) {
                pointList.add(nextCoordinate)
                subLeg.geometryForKotlin = GeoJsonLineString(pointList)
                result.add(subLeg)
                pointList.clear()
                subLeg = LegEntity()
            }
        }
        return result
    }
}