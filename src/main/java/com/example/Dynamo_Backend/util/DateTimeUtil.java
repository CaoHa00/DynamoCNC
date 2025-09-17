package com.example.Dynamo_Backend.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static long convertStringToTimestamp(String dateTimeStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, FORMATTER);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long convertStringToTimestampDate(String dateTimeStr) {
        LocalDate localDate = LocalDate.parse(dateTimeStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String convertTimestampToString(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(FORMATTER);
    }

    public static String convertTimestampToStringDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}