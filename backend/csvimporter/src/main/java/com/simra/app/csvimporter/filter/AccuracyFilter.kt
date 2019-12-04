package com.simra.app.csvimporter.filter

import com.simra.app.csvimporter.model.RideCSV


class AccuracyFilter {
    companion object {
        fun accuracyFilter(rideBean: List<RideCSV>, minAccuracy: Float): List<RideCSV> {
            return rideBean.filter { if (it.acc != null) it.acc.toFloat() <= minAccuracy else true }
        }
    }
}