package com.simra.app.csvimporter.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

public class Utils {

    private static final SimpleDateFormat weekdayPattern = new SimpleDateFormat("E", Locale.US);

    private static final double R = 6371d; // Radius of the earth


    public static int getMinuteOfDay(long ts) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.of("Europe/Paris"));
        int hours = zdt.getHour();
        int minutes = zdt.getMinute();
        return hours * 60 + minutes;
    }

    public static String getWeekday(long ts) {
        Date tsDate = new Date(ts);
        return weekdayPattern.format(tsDate);
    }

    // in km/h
    public static double calcAverageSpeed(Float distanceInMeters, Long durationInMillis) {
        return distanceInMeters / (durationInMillis / 1000d) * 3.6;
    }

    public static Double calcDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000.0;
    }

}
