package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.HistoryProcessDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffDetailStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffWorkingStatisticDto;

public interface StaffDetailStatisticService {
    StaffDetailStatisticDto getStaffDetailStatistic(StatisticRequestDto dto);

    List<HistoryProcessDto> getStaffHistoryProcesses(StatisticRequestDto dto);

    StaffWorkingStatisticDto getStaffWorkingStatistic(StatisticRequestDto dto);
}
