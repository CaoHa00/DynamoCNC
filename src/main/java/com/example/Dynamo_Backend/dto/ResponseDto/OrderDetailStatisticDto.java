package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderDetailStatisticDto {
    private Integer numberOfOrderDetails;
    private Integer numberOfProcess;
    private float totalTime;
    private float orderDetailRate;
    private float timeRate;
    private float processRate;
    private Map<String, Float> processPieChart;
    private Map<String, Float> groupPieChart;
}
