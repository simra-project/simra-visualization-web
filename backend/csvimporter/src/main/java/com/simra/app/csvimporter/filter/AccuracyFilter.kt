package main.java.com.simra.app.csvimporter.filter

import main.java.com.simra.app.csvimporter.model.RideCSV

class AccuracyFilter {
    companion object {
        fun accuracyFilter(rideBean: List<RideCSV>, minAccuracy: Float): List<RideCSV> {
            return rideBean.filter { if (it.acc != null) it.acc.toFloat() <= minAccuracy else true}
        }
    }
}