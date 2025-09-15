package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.HistoryProcessDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineDetailStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineEfficiencyResponseDto;

public interface MachineDetailStatisticService {
    MachineDetailStatisticDto getMachineDetailStatistic(StatisticRequestDto requestDto);

    List<HistoryProcessDto> getMachineHistoryProcess(StatisticRequestDto requestDto);

    MachineEfficiencyResponseDto getMachineEfficiency(StatisticRequestDto requestDto);

}
