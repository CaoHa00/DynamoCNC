package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.CurrentStaffDto;

public interface CurrentStaffService {

    CurrentStaffDto addCurrentStaff(CurrentStaffDto currentStaffRequestDto);

    CurrentStaffDto updateCurrentStaff(Long Id, CurrentStaffDto currentStaffDto);

    void deleteCurrentStaff(Long Id);

    List<CurrentStaffDto> getAllCurrentStaffs();

    CurrentStaffDto getCurrentStaffById(Long Id);

    CurrentStaffDto getCurrentStaffByMachineId(Integer machineId);

}
