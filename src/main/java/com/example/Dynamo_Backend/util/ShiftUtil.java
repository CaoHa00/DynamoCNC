package com.example.Dynamo_Backend.util;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.Dynamo_Backend.entities.ShiftInfo;

public class ShiftUtil {
    public static ShiftInfo resolveShift(LocalDateTime timestamp) {
        LocalTime time = timestamp.toLocalTime();
        LocalDate date = timestamp.toLocalDate();

        if (!time.isBefore(LocalTime.of(7, 0)) && time.isBefore(LocalTime.of(19, 0))) {
            // 07:00 - 18:59
            return new ShiftInfo("CA_NGAY", date);
        } else {
            // 19:00 - 23:59 OR 00:00 - 06:59
            if (time.isBefore(LocalTime.of(7, 0))) {
                date = date.minusDays(1);
            }
            return new ShiftInfo("CA_DEM", date);
        }
    }

    public static LocalDateTime getShiftEnd(LocalDateTime startTime) {
        LocalTime time = startTime.toLocalTime();
        LocalDate date = startTime.toLocalDate();

        // Ca ngày: 07:00 - 18:59 → kết thúc 19:00
        if (!time.isBefore(LocalTime.of(7, 0)) && time.isBefore(LocalTime.of(19, 0))) {
            return LocalDateTime.of(date, LocalTime.of(19, 0));
        }

        // Ca đêm
        // Trước 07:00 → ca đêm của ngày hôm trước, kết thúc 07:00 hôm nay
        if (time.isBefore(LocalTime.of(7, 0))) {
            return LocalDateTime.of(date, LocalTime.of(7, 0));
        }

        // Sau 19:00 → ca đêm kết thúc 07:00 ngày hôm sau
        return LocalDateTime.of(date.plusDays(1), LocalTime.of(7, 0));
    }
}
