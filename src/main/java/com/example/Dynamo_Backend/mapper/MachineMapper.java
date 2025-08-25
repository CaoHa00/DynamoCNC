package com.example.Dynamo_Backend.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.dto.RequestDto.MachineRequestDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class MachineMapper {
    public static Machine mapToMachine(MachineDto machineDto) {
        Machine machine = new Machine();
        machine.setMachineId(machineDto.getMachineId());
        machine.setMachineName(machineDto.getMachineName());
        machine.setMachineType(machineDto.getMachineType());
        machine.setMachineWork(machineDto.getMachineWork());
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
        machine.setMachineWork(machineDto.getMachineWork());
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
        machineDto.setMachineWork(machine.getMachineWork());
        machineDto.setMachineOffice(machine.getMachineOffice());
        machineDto.setStatus(machine.getStatus());
        machineDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(machine.getCreatedDate()));
        machineDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(machine.getCreatedDate()));
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        MachineKpi machineKpi = null;
        // if (machine.getMachineKpis() == null || machine.getMachineKpis().isEmpty()) {
        // machineDto.setMachineKpiDtos(null);
        // return machineDto;
        // }
        // for (MachineKpi mk : machine.getMachineKpis()) {
        // if (currentMonth.equals(String.format("%02d", mk.getMonth()))) {
        // machineKpi = mk;
        // }
        // }
        // if (machineKpi != null) {
        // machineDto.setMachineKpiDtos(MachineKpiMapper.mapToMachineKpiDto(machineKpi));
        // }

        String currentYear = String.valueOf(LocalDate.now().getYear());

        if (machine.getMachineKpis() != null && !machine.getMachineKpis().isEmpty()) {
            // 1. Try current month/year
            machineKpi = machine.getMachineKpis().stream()
                    .filter(mk -> currentMonth.equals(String.format("%02d", mk.getMonth())) &&
                            currentYear.equals(String.valueOf(mk.getYear())))
                    .findFirst()
                    .orElse(null);

            // 2. If not found, get the nearest previous month/year
            if (machineKpi == null) {
                machineKpi = machine.getMachineKpis().stream()
                        .filter(mk -> {
                            // Only earlier than current date
                            if (mk.getYear() < Integer.parseInt(currentYear))
                                return true;
                            if (mk.getYear() == Integer.parseInt(currentYear) &&
                                    mk.getMonth() < Integer.parseInt(currentMonth))
                                return true;
                            return false;
                        })
                        .max(Comparator.comparingInt((MachineKpi mk) -> mk.getYear())
                                .thenComparingInt(MachineKpi::getMonth))
                        .orElse(null);
            }

            if (machineKpi != null) {
                machineDto.setMachineKpiDtos(MachineKpiMapper.mapToMachineKpiDto(machineKpi));
            } else {
                machineDto.setMachineKpiDtos(null);
            }
        } else {
            machineDto.setMachineKpiDtos(null);
        }

        return machineDto;

    }

    public static MachineDto mapOnlyMachineName(Machine machine) {
        if (machine == null) {
            return null;
        } else {
            return new MachineDto(machine.getMachineName());
        }
    }
}
