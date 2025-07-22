package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.RequestDto.MachineRequestDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.util.DateTimeUtil;

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

    public static Machine mapToMachine(MachineRequestDto machineDto) {
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
        machineDto.setMachineId(machine.getMachineId());
        machineDto.setMachineName(machine.getMachineName());
        machineDto.setMachineType(machine.getMachineType());
        machineDto.setMachineGroup(machine.getMachineGroup());
        machineDto.setMachineOffice(machine.getMachineOffice());
        machineDto.setStatus(machine.getStatus());
        machineDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(machine.getCreatedDate()));
        machineDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(machine.getCreatedDate()));
        machineDto.setMachineKpiDtos(
                machine.getMachineKpis().stream().map(MachineKpiMapper::mapToMachineKpiDto).toList());
        return machineDto;

    }
}
