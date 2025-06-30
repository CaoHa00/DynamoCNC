package com.example.Dynamo_Backend.dto;

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
    private long createdDate;
    private long updatedDate;
}
