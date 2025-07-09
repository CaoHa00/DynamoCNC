package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StaffKpiDto {
    private Integer Id;
    private String duration;
    private Integer year;
    private Integer month;
    private Integer week;
    private Float pgTimeGoal;
    private Float machineTimeGoal;
    private Float manufacturingPoint;
    private Float oleGoal;
    private Float kpi;
    private String createdDate;
    private String updatedDate;
    private String staffId;
}
