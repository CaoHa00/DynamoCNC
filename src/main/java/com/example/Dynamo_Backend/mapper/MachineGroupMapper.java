package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.MachineGroupDto;
import com.example.Dynamo_Backend.entities.MachineGroup;

public class MachineGroupMapper {
    public static MachineGroupDto mapToMachineGroupDto(MachineGroup machineGroup) {
        MachineGroupDto machineGroupDto = new MachineGroupDto();
        machineGroupDto.setMachineGroupId(machineGroup.getMachineGroupId());
        machineGroupDto.setGroupId(machineGroup.getGroup().getGroupId());
        machineGroupDto.setMachineId(machineGroup.getMachine().getMachineId());
        machineGroupDto.setMachineName(machineGroup.getMachine().getMachineName());
        machineGroupDto.setCreatedDate(machineGroup.getCreatedDate());
        machineGroupDto.setUpdatedDate(machineGroup.getUpdatedDate());
        return machineGroupDto;
    }

    public static MachineGroup mapToMachineGroup(MachineGroupDto machineGroupDto) {
        MachineGroup machineGroup = new MachineGroup();
        machineGroup.setMachineGroupId(machineGroupDto.getMachineGroupId());
        return machineGroup;
    }
}
