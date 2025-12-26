package com.example.Dynamo_Backend.entities;

import java.time.LocalDateTime;
import java.time.LocalTime;

public enum ShiftType {
    DAY, NIGHT;

    public static ShiftType from(LocalDateTime time) {
        LocalTime t = time.toLocalTime();
        LocalTime dayStart = LocalTime.of(7, 0);
        LocalTime nightStart = LocalTime.of(19, 0);

        if (!t.isBefore(dayStart) && t.isBefore(nightStart)) {
            return DAY;
        }
        return NIGHT;
    }
}
