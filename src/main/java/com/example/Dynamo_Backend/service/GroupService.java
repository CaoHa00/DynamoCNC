package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.GroupDto;

public interface GroupService {
    GroupDto addGroup(GroupDto groupDto);

    GroupDto updateGroup(String Id, GroupDto groupDto);

    GroupDto getGroupById(String Id);

    void deleteGroup(String Id);

    List<GroupDto> getGroups();

    List<GroupDto> getStaffStatusGroup();

    List<GroupDto> getGroupByGroupType(String groupType);

}
