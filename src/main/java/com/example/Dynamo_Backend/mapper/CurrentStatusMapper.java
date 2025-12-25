package com.example.Dynamo_Backend.mapper;

import org.springframework.stereotype.Component;

import com.example.Dynamo_Backend.dto.CurrentStatusDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.repository.MachineRepository;

@Component
public class CurrentStatusMapper {
    private final MachineRepository machineRepository;

    public CurrentStatusMapper(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public CurrentStatusDto mapToCurrentStatusDto(CurrentStatus currentStatus) {
        if (currentStatus == null) {
            return null;
        }
        CurrentStatusDto dto = new CurrentStatusDto();
        dto.setId(currentStatus.getId());
        dto.setMachineId(currentStatus.getMachineId());
        Machine machine = machineRepository.findById(dto.getMachineId()).orElse(null);
        dto.setMachineName(machine.getMachineName());
        dto.setStatus(currentStatus.getStatus());
        dto.setTime(currentStatus.getTime());
        dto.setProcessId(currentStatus.getProcessId());
        return dto;
    }
}
