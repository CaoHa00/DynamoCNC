package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.dto.RequestDto.StaffRequestDto;

public interface StaffService {
    StaffDto addStaff(StaffRequestDto staffRequestDto);

    StaffDto updateStaff(String Id, StaffRequestDto staffDto);

    void deleteStaff(List<String> ids);

    List<StaffDto> getAllStaffs();

    StaffDto getStaffById(String Id);

}
