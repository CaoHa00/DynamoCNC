package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.StaffGroupDto;

public interface StaffGroupService {

    StaffGroupDto addStaffGroup(StaffGroupDto staffGroupDto);

    StaffGroupDto updateStaffGroup(String Id, StaffGroupDto staffGroupDtoDto);

    StaffGroupDto getStaffGroupById(String Id);

    void deleteStaffGroup(String Id);

    List<StaffGroupDto> getStaffGroups();
}
