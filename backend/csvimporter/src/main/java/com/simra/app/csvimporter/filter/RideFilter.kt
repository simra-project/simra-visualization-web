package main.java.com.simra.app.csvimporter.filter

import main.java.com.simra.app.csvimporter.model.Ride
import main.java.com.simra.app.csvimporter.model.RideCSV
import org.apache.log4j.Logger

const val minAccuracy = 12f
const val rdpEpsilon = 0.0000001

class RideFilter {

    private val logger = Logger.getLogger(RideFilter::class.java)

    fun fillerRide(ride: Ride): Ride {
        val accFilterResult = AccuracyFilter.accuracyFilter(ride.rideBeans as List<RideCSV>, minAccuracy)
        logger.info("Accuracy Filter filtered ${ride.rideBeans.size - accFilterResult.size} Coordinates")

        val rdpFilterResult = RamerDouglasPeuckerFilter.douglasPeucker(accFilterResult, rdpEpsilon)
        logger.info("RDP Filter filtered ${accFilterResult.size - rdpFilterResult.size} Coordinates")
        ride.rideBeans = rdpFilterResult
        return ride
    }
}
