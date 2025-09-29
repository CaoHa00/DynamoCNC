package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.CurrentStatusDto;
import com.example.Dynamo_Backend.dto.ResponseDto.CurrentStatusResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.ListCurrentStaffStatusDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;

public interface CurrentStatusService {
    void addCurrentStatus(String payload);

    List<CurrentStatus> all();

    CurrentStatusDto addCurrentStatus(CurrentStatusDto currentStatusDto);

    CurrentStatusDto updateCurrentStatus(String id, CurrentStatusDto currentStatusDto);

    void deleteCurrentStatus(String currentStatusId);

    CurrentStatusDto getCurrentStatusById(String id);

    List<CurrentStatusDto> getAllCurrentStatus();

    List<CurrentStatusResponseDto> getCurrentStatusByGroupId(String groupId);

    List<ListCurrentStaffStatusDto> getCurrentStaffStatusByGroupId(String groupId);
}
