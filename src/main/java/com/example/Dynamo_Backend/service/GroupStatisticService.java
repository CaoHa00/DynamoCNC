package com.example.Dynamo_Backend.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupStatisticDto;

public interface GroupStatisticService {
    StaffGroupStatisticDto getGroupStatistic(GroupEfficiencyRequestDto requestDto);

    List<StaffGroupOverviewDto> getGroupOverview(GroupEfficiencyRequestDto requestDto);

    ByteArrayInputStream exportExcel(GroupEfficiencyRequestDto requestDto) throws IOException;
}
