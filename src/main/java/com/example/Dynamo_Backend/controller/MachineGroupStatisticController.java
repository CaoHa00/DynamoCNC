package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineGroupOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineGroupStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.TotalRunTimeResponse;
import com.example.Dynamo_Backend.repository.dto.MachineRunTimeDto;
import com.example.Dynamo_Backend.service.MachineGroupStatisticService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/machine-group-statistic")
@RestController
public class MachineGroupStatisticController {
    private final MachineGroupStatisticService groupStatisticService;

    @PostMapping("/overview")
    public ResponseEntity<List<MachineGroupOverviewDto>> getGroupOverview(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        List<MachineGroupOverviewDto> overview = groupStatisticService.getGroupOverview(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(overview);
    }

    @PostMapping("/statistic")
    public ResponseEntity<MachineGroupStatisticDto> getGroupStatistic(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        MachineGroupStatisticDto statistic = groupStatisticService.getGroupStatistic(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(statistic);
    }

    @PostMapping("/totalTime")
    public ResponseEntity<TotalRunTimeResponse> getGroupTotalRunTime(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        TotalRunTimeResponse response = groupStatisticService.getTotalRunTime(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/top-5")
    public ResponseEntity<List<MachineRunTimeDto>> getTop5(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        List<MachineRunTimeDto> response = groupStatisticService.getTop5GroupOverview(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
