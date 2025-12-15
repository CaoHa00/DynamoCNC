package com.example.Dynamo_Backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
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

    public StaffKpiDto(Integer Id, String staffName) {
        this.Id = Id;
        this.staffName = staffName;
    }
}
