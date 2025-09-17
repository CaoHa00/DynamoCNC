package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.List;

import com.example.Dynamo_Backend.dto.StaffKpiDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaffWorkingStatisticDto {
    private Integer staffId;
    private String staffName;
    private Integer manufacturingPoints;
    private Float manufacturingPointsGoal;
    private Float pgTime;
    private Float pgTimeGoal;
    private Float workingHours;
    private Float workingHoursGoal;
    private Float ole;
    private Float oleGoal;
    private Float kpi;
    private Float kpiGoal;

    private List<StaffKpiDto> staffDto;
}
