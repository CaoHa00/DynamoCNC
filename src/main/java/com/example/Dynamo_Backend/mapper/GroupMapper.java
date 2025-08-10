package com.example.Dynamo_Backend.mapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.dto.ResponseDto.GroupResponseDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.util.DateTimeUtil;

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
                dto.setGroupId(group.getGroupId());
                dto.setGroupName(group.getGroupName());
                dto.setGroupType(group.getGroupType());
                dto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(group.getCreatedDate()));
                dto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(group.getCreatedDate()));
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

        public static GroupResponseDto mapToGroupResponseDto(Group group) {
                GroupResponseDto dto = new GroupResponseDto();
                dto.setGroupId(group.getGroupId());
                dto.setGroupName(group.getGroupName());
                dto.setGroupType(group.getGroupType());
                dto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(group.getCreatedDate()));
                dto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(group.getCreatedDate()));
                return dto;
        }
}
