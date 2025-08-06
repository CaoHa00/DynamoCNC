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
    private Integer year;
    private Integer month;
    private Float oee;
    private String groupId;
    private Float machineMiningTarget;
    private String createdDate;
    private String updatedDate;
}
