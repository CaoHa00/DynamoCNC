package com.example.Dynamo_Backend.service.implementation;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.mapper.GroupMapper;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.service.GroupService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GroupImplementation implements GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Override
    public GroupDto addGroup(GroupDto groupDto) {
        Group group = GroupMapper.mapToGroup(groupDto);
        long createdTimestamp = System.currentTimeMillis();
        group.setCreatedDate(createdTimestamp);
        group.setUpdatedDate(createdTimestamp);
        Group saveGroup = groupRepository.save(group);
        return GroupMapper.mapToGroupDto(saveGroup);
    }

    @Override
    public GroupDto updateGroup(String Id, GroupDto groupDto) {
        Group group = groupRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Group is not found:" + Id));
        long createdTimestamp = System.currentTimeMillis();
        group.setCreatedDate(createdTimestamp);
        group.setUpdatedDate(createdTimestamp);
        group.setGroupId(groupDto.getGroupId());
        group.setGroupName(groupDto.getGroupName());
        Group updatedgroup = groupRepository.save(group);
        return GroupMapper.mapToGroupDto(updatedgroup);
    }

    @Override
    public GroupDto getGroupById(String Id) {
        Group group = groupRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Group is not found:" + Id));
        return GroupMapper.mapToGroupDto(group);
    }

    @Override
    public void deleteGroup(String Id) {
        Group group = groupRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Group is not found:" + Id));
        groupRepository.delete(group);
    }

    @Override
    public List<GroupDto> getGroups() {
        List<Group> groups = groupRepository.findAll();
        groups.sort(Comparator.comparing(group -> {
            String name = group.getGroupName(); // ví dụ: "Group 10"
            String numberPart = name.replaceAll("\\D+", ""); // -> "10"
            return Integer.parseInt(numberPart); // -> 10
        }));
        return groups.stream().map(GroupMapper::mapToGroupDto).toList();
    }

    @Override
    public List<GroupDto> getStaffStatusGroup() {
        List<Group> groups = groupRepository.findAll();
        groups.sort(Comparator.comparing(group -> {
            String name = group.getGroupName();
            String numberPart = name.replaceAll("\\D+", "");
            return Integer.parseInt(numberPart);
        }));

        return groups.stream()
                .map(group -> GroupMapper.mapToGroupStatusDto(group))
                .toList();
    }

}
