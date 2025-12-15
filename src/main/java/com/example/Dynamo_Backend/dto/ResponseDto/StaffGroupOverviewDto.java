package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class StaffGroupOverviewDto {
    private String staffId;
    private Integer staffIdNumber;
    private String staffFullName;
    private Float workingHourGoal;
    private Float totalWorkingHour;
    private Float manufacturingPointGoal;
    private Integer totalManufacturingPoint;
    private Integer totalOperationNumber;// số nguyên công
    private Float oleGoal;
    private Float ole;
    private Float kpiGoal;
    private Float kpi;//
    private Float machineTimeGoal;
    private Float machineTime;

    private Float pgTimeGoal;
    private Float pgTime;
}
