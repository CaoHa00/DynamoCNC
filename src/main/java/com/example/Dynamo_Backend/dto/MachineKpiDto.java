package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MachineKpiDto {
    private Integer Id;
    private Integer year;
    private Integer month;
    private Float oee;
    private Float machineMiningTarget;
    private String createdDate;
    private String updatedDate;
    private Integer machineId;
    private String machineName;
    private Integer machineStatus;
}
