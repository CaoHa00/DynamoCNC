package com.example.Dynamo_Backend.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffRequestDto {
    private String Id;
    private Integer staffId;
    private String staffName;
    private String staffOffice;
    private String staffSection;
    private String shortName;
    private Float kpi;
    private Integer status;
    private Integer year;
    private Integer month;
    private Float pgTimeGoal;
    private Float workGoal;
    private Float machineTimeGoal;
    private Float manufacturingPoint;
    private Float oleGoal;
    private String groupId;
    private String createdDate;
    private String updatedDate;
}
