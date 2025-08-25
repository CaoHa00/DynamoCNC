package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessStatistic;
import com.example.Dynamo_Backend.dto.ResponseDto.ProcessOverviewDto;

public interface ProcessStatisticService {
    DrawingCodeProcessStatistic getStatisticsForProcess(GroupEfficiencyRequestDto requestDto);

    List<ProcessOverviewDto> getProcessOverview(GroupEfficiencyRequestDto requestDto);
}
