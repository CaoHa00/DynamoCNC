package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProcessOverviewDto {
    private String orderCode;
    private Integer partNumber;
    private Integer stepNumber;
    private Float pgTime;
    private Float pgTimeDifference;
}
