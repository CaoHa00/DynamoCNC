package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupKpiDto {
    private Integer Id;
    private Integer year;
    private Integer month;
    private Integer week;
    private String office;
    private Integer workHoursAim;
    private Integer workHoursChange;
    private Integer realWorkHours;
    private String groupId;
    private String createdDate;
    private String updatedDate;

}
