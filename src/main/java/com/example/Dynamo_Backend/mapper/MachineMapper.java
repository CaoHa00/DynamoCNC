package com.example.Dynamo_Backend.mapper;

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
        machine.setAddDate(machineDto.getAddDate());
        return machine;

    }

    public static MachineDto mapToMachineDto(Machine machine) {
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineId(machine.getMachineId());
        machineDto.setMachineName(machine.getMachineName());
        machineDto.setMachineType(machine.getMachineType());
        machineDto.setMachineGroup(machine.getMachineGroup());
        machineDto.setMachineOffice(machine.getMachineOffice());
        machineDto.setStatus(machine.getStatus());
        machineDto.setAddDate(machine.getAddDate());
        return machineDto;

    }
}
