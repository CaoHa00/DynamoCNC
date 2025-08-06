package com.example.Dynamo_Backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.dto.ResponseDto.CurrentStatusResponseDto;

import jakarta.persistence.criteria.CriteriaBuilder.In;

public interface GroupService {
    GroupDto addGroup(GroupDto groupDto);

    GroupDto updateGroup(String Id, GroupDto groupDto);

    GroupDto getGroupById(String Id);

    void deleteGroup(String Id);

    List<GroupDto> getGroups();

    List<GroupDto> getStaffStatusGroup();

    List<GroupDto> getGroupByGroupType(String groupType);

    void importGroupFromExcel(MultipartFile file);

    Map<String, Long> getGroupCountByGroupId(String groupId);

}
