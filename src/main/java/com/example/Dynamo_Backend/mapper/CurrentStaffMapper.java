package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.CurrentStaffDto;
import com.example.Dynamo_Backend.entities.CurrentStaff;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class CurrentStaffMapper {
    public static CurrentStaffDto mapToCurrentStaffDto(CurrentStaff currentStaff) {
        CurrentStaffDto currentStaffDto = new CurrentStaffDto();
        currentStaffDto.setAssignedAt(DateTimeUtil.convertTimestampToString(currentStaff.getAssignedAt()));
        currentStaffDto.setMachineId(currentStaff.getMachine().getMachineId());
        currentStaffDto.setStaffId(currentStaff.getStaff().getId());

        return currentStaffDto;

    }
}
