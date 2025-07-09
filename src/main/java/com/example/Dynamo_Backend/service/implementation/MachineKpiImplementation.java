package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.MachineKpiDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.MachineKpiService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MachineKpiImplementation implements MachineKpiService {
    MachineKpiRepository machineKpiRepository;
    MachineRepository machineRepository;

    @Override
    public MachineKpiDto addMachineKpi(MachineKpiDto machineKpiDto) {
        long createdTimestamp = System.currentTimeMillis();
        Machine machine = machineRepository.findById(machineKpiDto.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + machineKpiDto.getMachineId()));
        MachineKpi machineKpi = MachineKpiMapper.mapToMachineKpi(machineKpiDto);
        machineKpi.setMachine(machine);
        machineKpi.setCreatedDate(createdTimestamp);
        machineKpi.setUpdatedDate(createdTimestamp);
        MachineKpi saveMachineKpi = machineKpiRepository.save(machineKpi);
        return MachineKpiMapper.mapToMachineKpiDto(saveMachineKpi);
    }

    @Override
    public MachineKpiDto updateMachineKpi(Integer Id, MachineKpiDto machineKpiDto) {
        MachineKpi machineKpi = machineKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        Machine machine = machineRepository.findById(machineKpiDto.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + machineKpiDto.getMachineId()));
        machineKpi.setMachine(machine);
        machineKpi.setDuration(machineKpiDto.getDuration());
        machineKpi.setYear(machineKpiDto.getYear());
        machineKpi.setMonth(machineKpiDto.getMonth());
        machineKpi.setWeek(machineKpiDto.getWeek());
        machineKpi.setMonthlyRunningTime(machineKpiDto.getMonthlyRunningTime());
        machineKpi.setWeeklyRunningTime(machineKpiDto.getWeeklyRunningTime());
        machineKpi.setOeeGoal(machineKpiDto.getOeeGoal());
        machineKpi.setUpdatedDate(updatedTimestamp);
        MachineKpi saveMachineKpi = machineKpiRepository.save(machineKpi);
        return MachineKpiMapper.mapToMachineKpiDto(saveMachineKpi);
    }

    @Override
    public MachineKpiDto getMachineKpiById(Integer Id) {
        MachineKpi machineKpi = machineKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        return MachineKpiMapper.mapToMachineKpiDto(machineKpi);
    }

    @Override
    public void deleteMachineKpi(Integer Id) {
        MachineKpi machineKpi = machineKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        machineKpiRepository.delete(machineKpi);
    }

    @Override
    public List<MachineKpiDto> getMachineKpis() {
        List<MachineKpi> machineKpis = machineKpiRepository.findAll();
        return machineKpis.stream().map(MachineKpiMapper::mapToMachineKpiDto).toList();
    }

}
