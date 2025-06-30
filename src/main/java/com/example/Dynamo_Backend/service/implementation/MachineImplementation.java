package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.mapper.MachineMapper;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.service.MachineService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MachineImplementation implements MachineService {
    @Autowired
    MachineRepository machineRepository;

    @Override
    public MachineDto addMachine(MachineDto machineDto) {
        int status = 1;
        long createdTimestamp = System.currentTimeMillis();
        Machine machine = MachineMapper.mapToMachine(machineDto);
        machine.setStatus(status);
        machine.setCreatedDate(createdTimestamp);
        machine.setUpdatedDate(createdTimestamp);
        Machine saveMachine = machineRepository.save(machine);
        return MachineMapper.mapToMachineDto(saveMachine);

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
    }

    @Override
    public List<MachineDto> getMachines() {
        List<Machine> machines = machineRepository.findAll();
        return machines.stream().map(MachineMapper::mapToMachineDto).toList();

    }

}
