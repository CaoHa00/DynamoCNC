package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorDto {
    private String Id;
    private Integer operatorId;
    private String operatorName;
    private String operatorOffice;
    private String operatorSection;
    private String operatorStep;
    private Double kpi;
    private Integer status;
    private String dateAdd;
}
