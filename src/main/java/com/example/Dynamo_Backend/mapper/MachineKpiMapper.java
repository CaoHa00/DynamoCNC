package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.MachineKpiDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.repository.MachineRepository;

public class MachineKpiMapper {
    MachineRepository machineRepository;

    public static MachineKpi mapToMachineKpi(MachineKpiDto machineKpiDto) {
        MachineKpi machineKpi = new MachineKpi();
        // Machine machine = machineRepository.findById(machineKpiDto.getMachineId())
        // .orElseThrow(() -> new RuntimeException("Machine is not found:" +
        // machineKpiDto.getMachineId()));
        // machineKpi.setMachine(machine);
        machineKpi.setDuration(machineKpiDto.getDuration());
        machineKpi.setYear(machineKpiDto.getYear());
        machineKpi.setMonth(machineKpiDto.getMonth());
        machineKpi.setWeek(machineKpiDto.getWeek());
        machineKpi.setMonthlyRunningTime(machineKpiDto.getMonthlyRunningTime());
        machineKpi.setWeeklyRunningTime(machineKpiDto.getWeeklyRunningTime());
        machineKpi.setOeeGoal(machineKpiDto.getOeeGoal());
        // machineKpi.setCreatedDate((long) 0);
        // machineKpi.setUpdatedDate((long) 0);
        return machineKpi;
    }

    public static MachineKpiDto mapToMachineKpiDto(MachineKpi machineKpi) {
        MachineKpiDto machineKpiDto = new MachineKpiDto();
        String formattedCreatedDate = Instant.ofEpochMilli(machineKpi.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(machineKpi.getUpdatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        machineKpiDto.setCreatedDate(formattedCreatedDate);
        machineKpiDto.setUpdatedDate(formattedUpdatedDate);
        machineKpiDto.setMachineId(machineKpi.getMachine().getMachineId());
        machineKpiDto.setDuration(machineKpi.getDuration());
        machineKpiDto.setYear(machineKpi.getYear());
        machineKpiDto.setMonth(machineKpi.getMonth());
        machineKpiDto.setWeek(machineKpi.getWeek());
        machineKpiDto.setMonthlyRunningTime(machineKpi.getMonthlyRunningTime());
        machineKpiDto.setWeeklyRunningTime(machineKpi.getWeeklyRunningTime());
        machineKpiDto.setOeeGoal(machineKpi.getOeeGoal());

        return machineKpiDto;
    }
}
