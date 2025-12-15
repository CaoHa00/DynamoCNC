package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderCodeOverviewDto {
    private String orderCode;
    private Integer pgTimeGoal;
    private Float pgTime;
    private Float diffTime;

}
