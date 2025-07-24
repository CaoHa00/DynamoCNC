package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.CurrentStaffDto;

public interface CurrentStaffService {
    CurrentStaffDto addCurrentStaff(CurrentStaffDto currentStaffRequestDto);

    CurrentStaffDto updateCurrentStaff(Integer Id, CurrentStaffDto currentStaffDto);

    void deleteCurrentStaff(Integer Id);

    List<CurrentStaffDto> getAllCurrentStaffs();

    CurrentStaffDto getCurrentStaffById(Integer Id);
}
