package com.example.Dynamo_Backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
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
    private String groupId;
    private String groupName;

    public MachineKpiDto(Integer machineId, String machineName) {
        this.machineId = machineId;
        this.machineName = machineName;
    }
}
