package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupStatisticDto;

public interface StaffGroupStatisticService {
    StaffGroupStatisticDto getStaffGroupStatistic(GroupEfficiencyRequestDto requestDto);

    List<StaffGroupOverviewDto> getStaffGroupOverview(GroupEfficiencyRequestDto requestDto);
}
