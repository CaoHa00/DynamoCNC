package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.MachineKpiDto;

public interface MachineKpiService {
    MachineKpiDto addMachineKpi(MachineKpiDto machineKpiDto);

    MachineKpiDto updateMachineKpi(Integer Id, MachineKpiDto machineKpiDto);

    MachineKpiDto updateMachineKpiByMachineId(Integer machineId, MachineKpiDto machineKpiDto);

    MachineKpiDto getMachineKpiById(Integer Id);

    void deleteMachineKpi(Integer Id);

    List<MachineKpiDto> getMachineKpis();
}
