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
    private Integer isMonth;
    private String office;
    private Integer workingHourGoal;
    private Integer workingHourDifference;
    private Integer workingHour;
    private String groupId;
    private String createdDate;
    private String updatedDate;

}
