package com.simra.app.csvimporter.filter

import com.simra.app.csvimporter.model.RideCSV
import org.apache.log4j.Logger

open class RideFilter(private val minAccuracy: Float, private val rdpEpsilon: Double) {

    private val logger = Logger.getLogger(RideFilter::class.java)

    fun filterRide(rideBeans: List<RideCSV>): List<RideCSV> {
        val accFilterResult = AccuracyFilter.accuracyFilter(rideBeans, minAccuracy)
        logger.info("Accuracy Filter filtered ${rideBeans.size - accFilterResult.size} Coordinates")

        val rdpFilterResult = RamerDouglasPeuckerFilter.douglasPeucker(accFilterResult, rdpEpsilon)
        logger.info("RDP Filter filtered ${accFilterResult.size - rdpFilterResult.size} Coordinates")
        return rdpFilterResult
    }
}
