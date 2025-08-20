package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineGroupOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.MachineGroupStatisticDto;
import com.example.Dynamo_Backend.service.MachineGroupStatisticService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/machine-group-statistic")
@RestController
public class MachineGroupStatisticController {
    private final MachineGroupStatisticService groupStatisticService;

    @GetMapping("/overview")
    public ResponseEntity<List<MachineGroupOverviewDto>> getGroupOverview(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        List<MachineGroupOverviewDto> overview = groupStatisticService.getGroupOverview(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(overview);
    }

    @GetMapping("/statistic")
    public ResponseEntity<MachineGroupStatisticDto> getGroupStatistic(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        MachineGroupStatisticDto statistic = groupStatisticService.getGroupStatistic(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(statistic);
    }
}
