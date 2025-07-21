package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.MachineKpiDto;
import com.example.Dynamo_Backend.dto.RequestDto.MachineRequestDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.mapper.MachineKpiMapper;
import com.example.Dynamo_Backend.mapper.MachineMapper;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.MachineKpiService;
import com.example.Dynamo_Backend.service.MachineService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MachineImplementation implements MachineService {
    @Autowired
    MachineRepository machineRepository;

    MachineKpiRepository machineKpiRepository;
    MachineKpiService machineKpiService;

    @Override
    public MachineDto addMachine(MachineRequestDto machineDto) {
        int status = 1;
        long createdTimestamp = System.currentTimeMillis();
        Machine machine = MachineMapper.mapToMachine(machineDto);
        machine.setStatus(status);
        machine.setCreatedDate(createdTimestamp);
        machine.setUpdatedDate(createdTimestamp);
        machine.setMachineKpis(new ArrayList<MachineKpi>());
        Machine saveMachine = machineRepository.save(machine);

        machineDto.setMachineId(saveMachine.getMachineId());
        MachineKpiDto machineKpiDto = MachineKpiMapper.mapToMachineKpiDto(machineDto);
        MachineKpiDto saveMachineKpiDto = machineKpiService.addMachineKpi(machineKpiDto);

        MachineDto result = MachineMapper.mapToMachineDto(saveMachine);
        result.setMachineKpiDtos(new ArrayList<MachineKpiDto>());
        result.getMachineKpiDtos().add(saveMachineKpiDto);

        // return MachineMapper.mapToMachineDto(saveMachine);
        return result;

    }

    @Override
    public MachineDto updateMachine(Integer Id, MachineDto machineDto) {
        Machine machine = machineRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        machine.setMachineGroup(machineDto.getMachineGroup());
        machine.setMachineName(machineDto.getMachineName());
        machine.setMachineOffice(machineDto.getMachineOffice());
        machine.setMachineType(machineDto.getMachineType());
        machine.setStatus(machineDto.getStatus());
        machine.setUpdatedDate(updatedTimestamp);
        Machine updatedMachine = machineRepository.save(machine);
        return MachineMapper.mapToMachineDto(updatedMachine);
    }

    @Override
    public MachineDto getMachineById(Integer Id) {
        Machine machine = machineRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        return MachineMapper.mapToMachineDto(machine);
    }

    @Override
    public void deleteMachine(Integer Id) {
        Machine machine = machineRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + Id));
        machineRepository.delete(machine);
        List<MachineKpi> machineKpis = machineKpiRepository.findByMachine_machineId(Id);
        for (MachineKpi machineKpi : machineKpis) {
            machineKpiRepository.delete(machineKpi);
        }
    }

    @Override
    public List<MachineDto> getMachines() {
        List<Machine> machines = machineRepository.findAll();
        return machines.stream().map(MachineMapper::mapToMachineDto).toList();

    }

}
