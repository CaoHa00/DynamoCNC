package com.example.Dynamo_Backend.dto;

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
}