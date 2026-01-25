package com.example.Dynamo_Backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimePeriodInfo {
    private boolean isMonth;
    private Integer week; // nullable if isMonth=true
    private Integer month;
    private Integer year;
    private long day; // nullable if isMonth=true
    private Long startDate;
    private Long endDate;
    private LocalDate start;
    private LocalDate end;
    private Integer weekOfYear;
}