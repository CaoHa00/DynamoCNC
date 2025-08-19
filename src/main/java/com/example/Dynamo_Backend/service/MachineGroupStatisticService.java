package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineGroupOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineGroupStatisticDto;

public interface MachineGroupStatisticService {
    MachineGroupStatisticDto getGroupStatistic(GroupEfficiencyRequestDto requestDto);

    List<MachineGroupOverviewDto> getGroupOverview(GroupEfficiencyRequestDto requestDto);
}
