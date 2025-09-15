package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.List;

import com.example.Dynamo_Backend.dto.MachineKpiDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MachineEfficiencyResponseDto {
    private Integer machineId;
    private String machineName;
    private float operationalEfficiency;
    private float pgEfficiency;
    private float valueEfficiency;
    private float oee;
    private float offsetLoss;
    private float otherLoss;

    private List<MachineKpiDto> machines;
}
