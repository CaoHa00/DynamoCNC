package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MachineGroupOverviewDto {
    private Integer machineId;
    private String machineName;
    private Float runTime;
    private Float stopTime;
    private Float pgTime;
    private Float offsetTime;
    private Float spanTime;
    private Float pgTimeExpect;
    private Integer numberOfProcesses;
}
