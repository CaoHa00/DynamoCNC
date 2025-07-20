package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.entities.Group;

public class GroupMapper {
        public static Group mapToGroup(GroupDto groupDto) {
                Group group = new Group();
                group.setGroupId(groupDto.getGroupId());
                group.setGroupName(groupDto.getGroupName());
                group.setGroupType(groupDto.getGroupType());
                return group;
        }

        public static GroupDto mapToGroupDto(Group group) {
                GroupDto dto = new GroupDto();
                String formattedCreatedDate = Instant.ofEpochMilli(group.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String formattedUpdatedDate = Instant.ofEpochMilli(group.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dto.setGroupId(group.getGroupId());
                dto.setGroupName(group.getGroupName());
                dto.setGroupType(group.getGroupType());
                dto.setCreatedDate(formattedCreatedDate);
                dto.setUpdatedDate(formattedUpdatedDate);
                dto.setStaffGroups(group.getStaffGroups() != null ? group.getStaffGroups().stream()
                                .map(StaffGroupMapper::mapToStaffGroupDto).collect(Collectors.toList())
                                : new ArrayList<>());

                dto.setMachineGroups(group.getMachineGroups() != null ? group.getMachineGroups().stream()
                                .map(MachineGroupMapper::mapToMachineGroupDto).collect(Collectors.toList())
                                : new ArrayList<>());
                return dto;
        }

        public static GroupDto mapToGroupStatusDto(Group group) {
                GroupDto dto = new GroupDto();
                dto.setGroupId(group.getGroupId());
                dto.setGroupName(group.getGroupName());
                dto.setGroupType(group.getGroupType());
                dto.setStaffGroups(group.getStaffGroups() != null ? group.getStaffGroups().stream()
                                .map(StaffGroupMapper::mapToStaffGroupDto).collect(Collectors.toList())
                                : new ArrayList<>());

                dto.setMachineGroups(group.getMachineGroups() != null ? group.getMachineGroups().stream()
                                .map(MachineGroupMapper::mapToMachineGroupDto).collect(Collectors.toList())
                                : new ArrayList<>());

                return dto;
        }
}
