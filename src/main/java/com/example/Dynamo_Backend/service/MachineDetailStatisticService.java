package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.RequestDto.MachineStatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.HistoryProcessDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineDetailStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineEfficiencyResponseDto;

public interface MachineDetailStatisticService {
    MachineDetailStatisticDto getMachineDetailStatistic(MachineStatisticRequestDto requestDto);

    List<HistoryProcessDto> getMachineHistoryProcess(MachineStatisticRequestDto requestDto);

    MachineEfficiencyResponseDto getMachineEfficiency(MachineStatisticRequestDto requestDto);

}
