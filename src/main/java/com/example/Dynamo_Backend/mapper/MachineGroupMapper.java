package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.MachineGroupDto;
import com.example.Dynamo_Backend.entities.MachineGroup;

public class MachineGroupMapper {
    public static MachineGroupDto mapToMachineGroupDto(MachineGroup machineGroup) {
        MachineGroupDto machineGroupDto = new MachineGroupDto();

        String formattedCreatedDate = Instant.ofEpochMilli(machineGroup.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(machineGroup.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        machineGroupDto.setMachineGroupId(machineGroup.getMachineGroupId());
        machineGroupDto.setGroupId(machineGroup.getGroup().getGroupId());
        machineGroupDto.setMachineId(machineGroup.getMachine().getMachineId());
        machineGroupDto.setMachineName(machineGroup.getMachine().getMachineName());
        machineGroupDto.setCreatedDate(formattedCreatedDate);
        machineGroupDto.setUpdatedDate(formattedUpdatedDate);
        return machineGroupDto;
    }

    public static MachineGroup mapToMachineGroup(MachineGroupDto machineGroupDto) {
        MachineGroup machineGroup = new MachineGroup();
        machineGroup.setMachineGroupId(machineGroupDto.getMachineGroupId());
        return machineGroup;
    }
}
