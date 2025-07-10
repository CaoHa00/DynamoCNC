package com.example.Dynamo_Backend.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MachineRequestDto {
    private Integer machineId;
    private String machineName;
    private String machineType;
    private String machineGroup;
    private String machineOffice;
    private int status;
    private String duration;
    private Integer year;
    private Integer month;
    private Integer week;
    private Float oeeGoal;
    private Integer monthlyRunningTime;
    private Integer weeklyRunningTime;
    private String createdDate;
    private String updatedDate;
}
