package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.StaffDto;

public interface StaffService {
    StaffDto addStaff(StaffDto staffDto);

    StaffDto updateStaff(String Id, StaffDto staffDto);

    void deleteStaff(List<String> ids);

    List<StaffDto> getAllStaffs();

    StaffDto getStaffById(String Id);

}
