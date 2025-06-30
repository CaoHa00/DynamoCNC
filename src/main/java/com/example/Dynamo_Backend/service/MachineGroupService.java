package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.MachineGroupDto;

public interface MachineGroupService {
    MachineGroupDto addMachineGroup(MachineGroupDto machineGroupDto);

    MachineGroupDto updateMachineGroup(String Id, MachineGroupDto machineGroupDto);

    MachineGroupDto getMachineGroupById(String Id);

    void deleteMachineGroup(String Id);

    List<MachineGroupDto> getMachineGroups();
}
