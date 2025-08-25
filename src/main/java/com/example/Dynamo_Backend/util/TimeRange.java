package com.example.Dynamo_Backend.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.exception.BusinessException;

public class TimeRange {
    public static TimePeriodInfo getRangeTypeAndWeek(GroupEfficiencyRequestDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate start = LocalDateTime.parse(dto.getStartDate(), formatter).toLocalDate();
        LocalDate end = LocalDateTime.parse(dto.getEndDate(), formatter).toLocalDate();
        Long startTimestamp = DateTimeUtil.convertStringToTimestamp(dto.getStartDate());
        Long endTimestamp = DateTimeUtil.convertStringToTimestamp(dto.getEndDate());
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        if (days < 1 || days > 31) {
            throw new BusinessException("Invalid date range");
        }
        if (days <= 7) {
            int weekOfMonth = end.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
            return new TimePeriodInfo(false, weekOfMonth, start.getMonthValue(), start.getYear(), days, startTimestamp,
                    endTimestamp);
        } else if (start.getDayOfMonth() == 1 && end.equals(start.withDayOfMonth(start.lengthOfMonth()))) {
            return new TimePeriodInfo(true, null, start.getMonthValue(), start.getYear(), days, startTimestamp,
                    endTimestamp);
        } else {
            throw new BusinessException("Invalid date range");
        }
    }

    public static TimePeriodInfo getPreviousTimeRange(TimePeriodInfo dto) {
        LocalDate prevStart = Instant.ofEpochMilli(dto.getStartDate())
                .atZone(ZoneId.systemDefault()).toLocalDate().minusDays(dto.getDay());
        LocalDate prevEnd = Instant.ofEpochMilli(dto.getEndDate())
                .atZone(ZoneId.systemDefault()).toLocalDate().minusDays(dto.getDay());

        // Set start at 00:00:00 and end at 23:59:59
        Long previousStartTime = prevStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long previousEndTime = prevEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if (dto.isMonth()) {
            int previousMonth = dto.getMonth() - 1;
            int previousYear = dto.getYear();
            if (previousMonth < 1) {
                previousMonth = 12;
                previousYear--;
            }
            return new TimePeriodInfo(true, null, previousMonth, previousYear, dto.getDay(),
                    previousStartTime, previousEndTime);
        } else {
            int previousWeek = dto.getWeek() - 1;
            int previousMonth = dto.getMonth();
            int previousYear = dto.getYear();
            if (previousWeek < 1) {
                previousMonth--;
                if (previousMonth < 1) {
                    previousMonth = 12;
                    previousYear--;
                }
                LocalDate lastDayOfPrevMonth = LocalDate.of(previousYear, previousMonth, 1)
                        .withDayOfMonth(LocalDate.of(previousYear, previousMonth, 1).lengthOfMonth());
                int maxWeek = lastDayOfPrevMonth.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
                previousWeek = maxWeek;
            }
            return new TimePeriodInfo(false, previousWeek, previousMonth, previousYear, dto.getDay(),
                    previousStartTime, previousEndTime);
        }
    }
}
