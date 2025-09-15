package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.*;
import com.example.Dynamo_Backend.service.MachineDetailStatisticService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/machine-detail")
public class MachineDetailStatisticController {
    private final MachineDetailStatisticService machineDetailStatisticService;

    @PostMapping("/statistic")
    public ResponseEntity<MachineDetailStatisticDto> getMachineDetailStatistic(
            @RequestBody StatisticRequestDto requestDto) {
        MachineDetailStatisticDto statisticDto = machineDetailStatisticService.getMachineDetailStatistic(requestDto);
        return ResponseEntity.ok(statisticDto);
    }

    @PostMapping("/history")
    public ResponseEntity<List<HistoryProcessDto>> getMachineHistoryProcess(
            @RequestBody StatisticRequestDto requestDto) {
        List<HistoryProcessDto> historyProcessDtos = machineDetailStatisticService.getMachineHistoryProcess(requestDto);
        return ResponseEntity.ok(historyProcessDtos);
    }

    @PostMapping("/efficiency")
    public ResponseEntity<MachineEfficiencyResponseDto> getMachineEfficiency(
            @RequestBody StatisticRequestDto requestDto) {
        MachineEfficiencyResponseDto efficiency = machineDetailStatisticService.getMachineEfficiency(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(efficiency);
    }
}
