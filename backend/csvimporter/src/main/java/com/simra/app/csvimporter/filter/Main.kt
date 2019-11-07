package main.java.com.simra.app.csvimporter.filter

import main.java.com.simra.app.csvimporter.model.RideCSV

fun main(rideBean: List<RideCSV>): List<RideCSV> {
    var result = AccuracyFilter.accuracyFilter(rideBean, 2.1f)
    result = RamerDouglasPeuckerFilter.douglasPeucker(rideBean, 0.1)
    return result
}