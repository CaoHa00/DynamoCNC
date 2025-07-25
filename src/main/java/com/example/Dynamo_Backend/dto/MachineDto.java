package com.example.Dynamo_Backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MachineDto {
    private Integer machineId;
    private String machineName;
    private String machineType;
    private String machineGroup;
    private String machineOffice;
    private int status;
    private String createdDate;
    private String updatedDate;
    private String groupId;
    private MachineKpiDto machineKpiDtos;
}
