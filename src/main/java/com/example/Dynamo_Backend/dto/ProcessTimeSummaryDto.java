package com.example.Dynamo_Backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessTimeSummaryDto {
    private Integer id;
    private Integer quantity;
    private Integer productionStep;
    private Float manufacturingPoint;
    private Float pgTime;
    private Float spanTime;
    private Float runTime;
    private Float stopTime;
    private Float offsetTime;
    private String orderCode;
}