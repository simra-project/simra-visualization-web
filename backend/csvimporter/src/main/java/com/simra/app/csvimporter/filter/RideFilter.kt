package main.java.com.simra.app.csvimporter.filter

import main.java.com.simra.app.csvimporter.model.Ride
import main.java.com.simra.app.csvimporter.model.RideCSV
import org.apache.log4j.Logger

class RideFilter(var minAccuracy: Float, var rdpEpsilon: Double) {

    private val logger = Logger.getLogger(RideFilter::class.java)

    fun filterRide(ride: Ride): List<RideCSV> {
        val accFilterResult = AccuracyFilter.accuracyFilter(ride.rideBeans as List<RideCSV>, minAccuracy)
        logger.info("Accuracy Filter filtered ${ride.rideBeans.size - accFilterResult.size} Coordinates")

        val rdpFilterResult = RamerDouglasPeuckerFilter.douglasPeucker(accFilterResult, rdpEpsilon)
        logger.info("RDP Filter filtered ${accFilterResult.size - rdpFilterResult.size} Coordinates")
        return rdpFilterResult
    }
}
