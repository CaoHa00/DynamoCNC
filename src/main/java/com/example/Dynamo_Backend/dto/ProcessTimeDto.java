package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProcessTimeDto {
    private Integer Id;
    private Float spanTime;
    private Float runTime;
    private Float pgTime;
    private Float stopTime;
    private Float offsetTime;
    private String drawingCodeProcessId;
}
