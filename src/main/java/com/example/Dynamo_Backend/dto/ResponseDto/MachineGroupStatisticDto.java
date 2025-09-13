package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.List;

import com.example.Dynamo_Backend.dto.MachineKpiDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MachineGroupStatisticDto {
    private String groupId;
    private String groupName;
    private Float totalRunTime;
    private Float totalStopTime;
    private Float totalPgTime;
    private Float totalOffsetTime;
    private Float totalSpanTime;
    private Float totalErrorTime;
    private Float errorTimeRate;
    private Float runTimeRate;
    private Float stopTimeRate;
    private Float pgTimeRate;
    private Float offsetTimeRate;
    private Float spanTimeRate;

    private List<MachineKpiDto> machines;
}
