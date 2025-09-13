package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MachineDetailStatisticDto {
    private Integer machineId;
    private String machineName;
    private Float totalRunTime;
    private Float runTimeRate;
    private Float totalStopTime;
    private Float stopTimeRate;
    private Float totalPgTime;
    private Float pgTimeRate;
    private Float totalErrorTime;
    private Float errorTimeRate;
    private Integer numberOfProcesses;
    private Float processRate;
    private Float totalOffsetTime;
}
