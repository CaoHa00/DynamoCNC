package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.MachineKpiDto;
import com.example.Dynamo_Backend.dto.RequestDto.MachineRequestDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class MachineKpiMapper {
    MachineRepository machineRepository;

    public static MachineKpi mapToMachineKpi(MachineKpiDto machineKpiDto) {
        MachineKpi machineKpi = new MachineKpi();
        // Machine machine = machineRepository.findById(machineKpiDto.getMachineId())
        // .orElseThrow(() -> new RuntimeException("Machine is not found:" +
        // machineKpiDto.getMachineId()));
        // machineKpi.setMachine(machine);
        machineKpi.setMachineMiningTarget(machineKpiDto.getMachineMiningTarget());
        machineKpi.setYear(machineKpiDto.getYear());
        machineKpi.setMonth(machineKpiDto.getMonth());
        machineKpi.setOee(machineKpiDto.getOee());
        // machineKpi.setCreatedDate((long) 0);
        // machineKpi.setUpdatedDate((long) 0);
        return machineKpi;
    }

    public static MachineKpiDto mapToMachineKpiDto(MachineKpi machineKpi) {
        MachineKpiDto machineKpiDto = new MachineKpiDto();
        machineKpiDto.setId(machineKpi.getId());
        machineKpiDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(machineKpi.getCreatedDate()));
        machineKpiDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(machineKpi.getCreatedDate()));
        machineKpiDto.setMachineId(machineKpi.getMachine().getMachineId());
        machineKpiDto.setYear(machineKpi.getYear());
        machineKpiDto.setMonth(machineKpi.getMonth());
        machineKpiDto.setMachineMiningTarget(machineKpi.getMachineMiningTarget());
        machineKpiDto.setOee(machineKpi.getOee());

        return machineKpiDto;
    }

    public static MachineKpiDto mapToMachineKpiDto(MachineRequestDto machineKpi) {
        MachineKpiDto machineKpiDto = new MachineKpiDto();

        machineKpiDto.setMachineId(machineKpi.getMachineId());
        machineKpiDto.setYear(machineKpi.getYear());
        machineKpiDto.setMonth(machineKpi.getMonth());
        machineKpiDto.setMachineMiningTarget(machineKpi.getMachineMiningTarget());
        machineKpiDto.setOee(machineKpi.getOee());

        return machineKpiDto;
    }
}
