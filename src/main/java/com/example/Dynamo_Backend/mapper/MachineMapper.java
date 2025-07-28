package com.example.Dynamo_Backend.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.RequestDto.MachineRequestDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
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
        machineDto.setGroupId(machine.getGroup().getGroupId());
        machineDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(machine.getCreatedDate()));
        machineDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(machine.getCreatedDate()));
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        MachineKpi machineKpi = null;
        for (MachineKpi mk : machine.getMachineKpis()) {
            if (currentMonth.equals(String.format("%02d", mk.getMonth()))) {
                machineKpi = mk;
            }
        }
        if (machineKpi != null) {
            machineDto.setMachineKpiDtos(MachineKpiMapper.mapToMachineKpiDto(machineKpi));
        }

        return machineDto;

    }
}
