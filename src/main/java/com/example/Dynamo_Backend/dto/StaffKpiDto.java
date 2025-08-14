package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StaffKpiDto {
    private Integer kpiId;
    private Integer year;
    private Integer month;
    private Float pgTimeGoal;
    private Float machineTimeGoal;
    private Float manufacturingPoint;
    private Float oleGoal;
    private Float workGoal;
    private Float kpi;
    private String createdDate;
    private String updatedDate;
    private String staffId;
    private Integer Id;
    private String staffName;
    private String groupId;
    private String groupName;
    private Integer staffStatus;
}
