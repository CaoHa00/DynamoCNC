package com.example.Dynamo_Backend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MachineKpiDto {
    private Integer Id;
    private String duration;
    private Integer year;
    private Integer month;
    private Integer week;
    private Float oeeGoal;
    private Integer monthlyRunningTime;
    private Integer weeklyRunningTime;
    private String createdDate;
    private String updatedDate;
    private Integer machineId;
}
