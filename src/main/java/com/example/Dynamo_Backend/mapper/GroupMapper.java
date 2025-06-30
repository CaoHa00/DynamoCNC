package com.example.Dynamo_Backend.mapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.entities.Group;

public class GroupMapper {
    public static Group mapToGroup(GroupDto groupDto) {
        Group group = new Group();
        group.setGroupId(groupDto.getGroupId());
        group.setGroupName(groupDto.getGroupName());
        return group;
    }

    public static GroupDto mapToGroupDto(Group group) {
        GroupDto dto = new GroupDto();
        dto.setGroupId(group.getGroupId());
        dto.setGroupName(group.getGroupName());
        dto.setOperatorGroups(group.getOperatorGroups() != null ? group.getOperatorGroups().stream()
                .map(OperatorGroupMapper::mapToOperatorGroupDto).collect(Collectors.toList()) : new ArrayList<>());

        dto.setMachineGroups(group.getMachineGroups() != null ? group.getMachineGroups().stream()
                .map(MachineGroupMapper::mapToMachineGroupDto).collect(Collectors.toList()) : new ArrayList<>());
        return dto;
    }

    public static GroupDto mapToGroupStatusDto(Group group) {
        GroupDto dto = new GroupDto();
        dto.setGroupId(group.getGroupId());
        dto.setGroupName(group.getGroupName());
        dto.setOperatorGroups(group.getOperatorGroups() != null ? group.getOperatorGroups().stream()
                .map(OperatorGroupMapper::mapToOperatorGroupDto).collect(Collectors.toList()) : new ArrayList<>());

        dto.setMachineGroups(group.getMachineGroups() != null ? group.getMachineGroups().stream()
                .map(MachineGroupMapper::mapToMachineGroupDto).collect(Collectors.toList()) : new ArrayList<>());

        return dto;
    }
}
