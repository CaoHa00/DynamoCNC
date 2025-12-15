package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MachineDto {
    private Integer machineId;
    private String machineName;
    private String machineType;
    private String machineWork;
    private String machineOffice;
    private Integer status;
    private String createdDate;
    private String updatedDate;
    private MachineKpiDto machineKpiDtos;

    public MachineDto(Integer machineId, String machineName) {
        this.machineId = machineId;
        this.machineName = machineName;
    }

}
