package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.CurrentStaffDto;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.CurrentStaff;
import com.example.Dynamo_Backend.mapper.CurrentStaffMapper;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.repository.CurrentStaffRepository;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.service.CurrentStaffService;

@Service
public class CurrentStaffImplementation implements CurrentStaffService {
    @Autowired
    private CurrentStaffRepository currentStaffRepository;

    @Autowired
    MachineRepository machineRepository;

    @Autowired
    StaffRepository staffRepository;

    @Override
    public CurrentStaffDto addCurrentStaff(CurrentStaffDto currentStaffDto) {
        CurrentStaff currentStaff = currentStaffRepository.findByMachine_MachineId(currentStaffDto.getMachineId());
        Staff staff = staffRepository.findById(currentStaffDto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + currentStaffDto.getStaffId()));
        Machine machine = machineRepository.findById(currentStaffDto.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine is not found:" + currentStaffDto.getMachineId()));
        if (currentStaff == null) {
            currentStaff = new CurrentStaff();
            currentStaff.setMachine(machine);
        }
        currentStaff.setAssignedAt(System.currentTimeMillis());
        currentStaff.setStaff(staff);
        CurrentStaff savedCurrentStaff = currentStaffRepository.save(currentStaff);

        return CurrentStaffMapper.mapToCurrentStaffDto(savedCurrentStaff);
    }

    @Override
    public CurrentStaffDto updateCurrentStaff(Long Id, CurrentStaffDto currentStaffDto) {
        CurrentStaff currentStaff = currentStaffRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("CurrentStaff is not found:" + Id));
        Staff staff = staffRepository.findById(currentStaffDto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + currentStaffDto.getStaffId()));
        currentStaff.setAssignedAt(System.currentTimeMillis());
        // just update staff
        currentStaff.setStaff(staff);

        CurrentStaff savedCurrentStaff = currentStaffRepository.save(currentStaff);

        return CurrentStaffMapper.mapToCurrentStaffDto(savedCurrentStaff);
    }

    @Override
    public void deleteCurrentStaff(Long Id) {
        CurrentStaff currentStaff = currentStaffRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("CurrentStaff is not found:" + Id));
        currentStaffRepository.delete(currentStaff);
    }

    @Override
    public List<CurrentStaffDto> getAllCurrentStaffs() {
        List<CurrentStaff> currentStaffs = currentStaffRepository.findAll();

        return currentStaffs.stream().map(CurrentStaffMapper::mapToCurrentStaffDto).toList();
    }

    @Override
    public CurrentStaffDto getCurrentStaffById(Long Id) {
        CurrentStaff currentStaff = currentStaffRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("CurrentStaff is not found:" + Id));

        return CurrentStaffMapper.mapToCurrentStaffDto(currentStaff);
    }

    @Override
    public CurrentStaffDto getCurrentStaffByMachineId(Integer machineId) {
        CurrentStaff currentStaff = currentStaffRepository.findByMachine_MachineId(machineId);
        return CurrentStaffMapper.mapToCurrentStaffDto(currentStaff);
    }

}
