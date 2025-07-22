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
    private String duration;
    private Integer year;
    private Integer month;
    private Integer week;
    private Float pgTimeGoal;
    private Float machineTimeGoal;
    private Float manufacturingPoint;
    private Float oleGoal;
    private String createdDate;
    private String updatedDate;
}
