package com.example.Dynamo_Backend.service;

import com.example.Dynamo_Backend.dto.TimePeriodInfo;
import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.GroupEfficiencyResponseDto;

public interface GroupEfficiencyService {
    GroupEfficiencyResponseDto getGroupEfficiency(GroupEfficiencyRequestDto requestDto);

    TimePeriodInfo getRangeTypeAndWeek(GroupEfficiencyRequestDto dto);
}
