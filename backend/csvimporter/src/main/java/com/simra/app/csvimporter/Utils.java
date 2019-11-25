package main.java.com.simra.app.csvimporter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class Utils {

    private static SimpleDateFormat weekdayPattern = new SimpleDateFormat("E");

    public static int getMinuteOfDay(long ts) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault());
        int hours = zdt.getHour();
        int minutes = zdt.getMinute();
        return hours * 60 + minutes;
    }

    public static String getWeekday(long ts) {
        Date tsDate = new Date(ts);
        return weekdayPattern.format(tsDate);
    }
}
