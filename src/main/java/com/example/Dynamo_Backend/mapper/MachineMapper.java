package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.entities.Machine;

public class MachineMapper {
    public static Machine mapToMachine(MachineDto machineDto) {
        Machine machine = new Machine();
        machine.setMachineId(machineDto.getMachineId());
        machine.setMachineName(machineDto.getMachineName());
        machine.setMachineType(machineDto.getMachineType());
        machine.setMachineGroup(machineDto.getMachineGroup());
        machine.setMachineOffice(machineDto.getMachineOffice());
        machine.setStatus(machineDto.getStatus());
        // machine.setCreatedDate(machineDto.getCreatedDate());
        // machine.setUpdatedDate(machineDto.getUpdatedDate());
        return machine;

    }

    public static MachineDto mapToMachineDto(Machine machine) {
        MachineDto machineDto = new MachineDto();
        String formattedCreatedDate = Instant.ofEpochMilli(machine.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(machine.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        machineDto.setMachineId(machine.getMachineId());
        machineDto.setMachineName(machine.getMachineName());
        machineDto.setMachineType(machine.getMachineType());
        machineDto.setMachineGroup(machine.getMachineGroup());
        machineDto.setMachineOffice(machine.getMachineOffice());
        machineDto.setStatus(machine.getStatus());
        machineDto.setCreatedDate(formattedCreatedDate);
        machineDto.setUpdatedDate(formattedUpdatedDate);
        return machineDto;

    }
}
