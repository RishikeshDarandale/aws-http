package io.github.rishikeshdarandale.aws.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility methods for date functionality
 * 
 * @author Rishikesh Darandale (Rishikesh.Darandale@gmail.com)
 *
 */
public class DateUtils {
    /**
     * Get the specified date in a specified format
     *
     * @param time - time to convert in the provided format
     * @param format - format of the date
     * @return - date in the specified format
     */
    public static String getDate(long time, String format) {
        ZonedDateTime utc = Instant.ofEpochMilli(time).atZone(ZoneOffset.UTC);
        return DateTimeFormatter.ofPattern(format).format(utc);
    }
}
