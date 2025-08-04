package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.CurrentStatusDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;

public class CurrentStatusMapper {
    public static CurrentStatusDto mapToCurrentStatusDto(CurrentStatus currentStatus) {
        if (currentStatus == null) {
            return null;
        }
        CurrentStatusDto dto = new CurrentStatusDto();
        dto.setId(currentStatus.getId());
        dto.setMachineId(currentStatus.getMachineId());
        dto.setStatus(currentStatus.getStatus());
        dto.setTime(currentStatus.getTime());
        return dto;
    }
}
