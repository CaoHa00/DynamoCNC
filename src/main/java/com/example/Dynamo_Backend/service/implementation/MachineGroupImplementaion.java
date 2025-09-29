package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.MachineGroupDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineGroup;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.GroupMapper;
import com.example.Dynamo_Backend.mapper.MachineGroupMapper;
import com.example.Dynamo_Backend.mapper.MachineMapper;
import com.example.Dynamo_Backend.repository.MachineGroupRepository;
import com.example.Dynamo_Backend.service.GroupService;
import com.example.Dynamo_Backend.service.MachineGroupService;
import com.example.Dynamo_Backend.service.MachineService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MachineGroupImplementaion implements MachineGroupService {

    public MachineGroupRepository machineGroupRepository;
    public GroupService groupService;
    public MachineService machineService;

    @Override
    public MachineGroupDto addMachineGroup(MachineGroupDto machineGroupDto) {
        MachineGroup machineGroup = MachineGroupMapper.mapToMachineGroup(machineGroupDto);
        MachineDto machine = machineService.getMachineById(machineGroupDto.getMachineId());
        GroupDto group = groupService.getGroupById(machineGroupDto.getGroupId());
        Group newGroup = GroupMapper.mapToGroup(group);
        Machine newMachine = MachineMapper.mapToMachine(machine);
        long createdTimestamp = System.currentTimeMillis();

        machineGroup.setGroup(newGroup);
        machineGroup.setMachine(newMachine);
        machineGroup.setCreatedDate(createdTimestamp);
        machineGroup.setUpdatedDate(createdTimestamp);

        MachineGroup saveMachineGroup = machineGroupRepository.save(machineGroup);
        return MachineGroupMapper.mapToMachineGroupDto(saveMachineGroup);

    }

    @Override
    public MachineGroupDto updateMachineGroup(String Id, MachineGroupDto machineGroupDto) {
        MachineGroup machineGroup = machineGroupRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("MachineGroup is not found:" + Id));
        GroupDto group = groupService.getGroupById(machineGroupDto.getGroupId());
        MachineDto machine = machineService.getMachineById(machineGroupDto.getMachineId());
        Group updateGroup = GroupMapper.mapToGroup(group);
        long updatedTimestamp = System.currentTimeMillis();

        Machine updateMachine = MachineMapper.mapToMachine(machine);
        machineGroup.setMachine(updateMachine);
        machineGroup.setGroup(updateGroup);
        machineGroup.setUpdatedDate(updatedTimestamp);
        MachineGroup updatedMachineGroup = machineGroupRepository.save(machineGroup);
        return MachineGroupMapper.mapToMachineGroupDto(updatedMachineGroup);
    }

    @Override
    public MachineGroupDto getMachineGroupById(String Id) {
        MachineGroup machineGroup = machineGroupRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("MachineGroup is not found:" + Id));
        return MachineGroupMapper.mapToMachineGroupDto(machineGroup);
    }

    @Override
    public void deleteMachineGroup(String Id) {
        MachineGroup machineGroup = machineGroupRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("MachineGroup is not found:" + Id));
        machineGroupRepository.delete(machineGroup);
    }

    @Override
    public List<MachineGroupDto> getMachineGroups() {
        List<MachineGroup> machineGroups = machineGroupRepository.findAll();
        return machineGroups.stream().map(MachineGroupMapper::mapToMachineGroupDto).toList();

    }
}
