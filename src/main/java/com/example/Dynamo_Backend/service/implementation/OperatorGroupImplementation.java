package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.dto.OperatorDto;
import com.example.Dynamo_Backend.dto.OperatorGroupDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Operator;
import com.example.Dynamo_Backend.entities.OperatorGroup;
import com.example.Dynamo_Backend.mapper.GroupMapper;

import com.example.Dynamo_Backend.mapper.OperatorGroupMapper;
import com.example.Dynamo_Backend.mapper.OperatorMapper;
import com.example.Dynamo_Backend.repository.OperatorGroupRepository;
import com.example.Dynamo_Backend.service.GroupService;
import com.example.Dynamo_Backend.service.OperatorGroupService;
import com.example.Dynamo_Backend.service.OperatorService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OperatorGroupImplementation implements OperatorGroupService {

    OperatorGroupRepository operatorGroupRepository;
    OperatorService operatorService;
    GroupService groupService;

    @Override
    public OperatorGroupDto addOperatorGroup(OperatorGroupDto operatorGroupDto) {

        OperatorGroup operatorGroup = OperatorGroupMapper.mapToOperatorGroup(operatorGroupDto);
        OperatorDto operator = operatorService.getOperatorById(operatorGroupDto.getOperatorId());

        GroupDto group = groupService.getGroupById(operatorGroupDto.getGroupId());
        Group newGroup = GroupMapper.mapToGroup(group);

        Operator newOperator = OperatorMapper.mapToOperator(operator);
        operatorGroup.setGroup(newGroup);
        operatorGroup.setOperator(newOperator);

        OperatorGroup saveOperatorGroup = operatorGroupRepository.save(operatorGroup);
        return OperatorGroupMapper.mapToOperatorGroupDto(saveOperatorGroup);
    }

    @Override
    public OperatorGroupDto updateOperatorGroup(String Id, OperatorGroupDto operatorGroupDto) {
        OperatorGroup operatorGroup = operatorGroupRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("operatorGroup is not found:" + Id));
        GroupDto group = groupService.getGroupById(operatorGroupDto.getGroupId());
        OperatorDto operator = operatorService.getOperatorById(operatorGroupDto.getOperatorId());
        Group updateGroup = GroupMapper.mapToGroup(group);
        Operator updateOperator = OperatorMapper.mapToOperator(operator);
        operatorGroup.setOperator(updateOperator);
        operatorGroup.setGroup(updateGroup);
        OperatorGroup updatedoperatorGroup = operatorGroupRepository.save(operatorGroup);
        return OperatorGroupMapper.mapToOperatorGroupDto(updatedoperatorGroup);
    }

    @Override
    public OperatorGroupDto getOperatorGroupById(String Id) {
        OperatorGroup operatorGroup = operatorGroupRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("operatorGroup is not found:" + Id));
        return OperatorGroupMapper.mapToOperatorGroupDto(operatorGroup);
    }

    @Override
    public void deleteOperatorGroup(String Id) {
        OperatorGroup operatorGroup = operatorGroupRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("OperatorGroup is not found:" + Id));
        operatorGroupRepository.delete(operatorGroup);
    }

    @Override
    public List<OperatorGroupDto> getOperatorGroups() {
        List<OperatorGroup> operatorGroups = operatorGroupRepository.findAll();
        return operatorGroups.stream().map(OperatorGroupMapper::mapToOperatorGroupDto).toList();
    }

}
