package com.company.employeemanagement.util;

import java.time.Duration;
import java.time.LocalTime;

public class DateUtils {

    private DateUtils() {}

    /**
     * Calculate session duration as HH:mm:ss string.
     */
    public static String calculateDuration(LocalTime loginTime, LocalTime logoutTime) {
        if (loginTime == null || logoutTime == null) return null;
        Duration duration = Duration.between(loginTime, logoutTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Parse "HH:mm:ss" duration string to total minutes.
     */
    public static long parseDurationToMinutes(String duration) {
        if (duration == null) return 0;
        String[] parts = duration.split(":");
        if (parts.length != 3) return 0;
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        return hours * 60 + minutes;
    }
}
