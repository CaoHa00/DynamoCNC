package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessStatistic;
import com.example.Dynamo_Backend.dto.ResponseDto.ProcessOverviewDto;
import com.example.Dynamo_Backend.service.ProcessStatisticService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/process-statistic")
public class ProcessStatisticController {
    private final ProcessStatisticService processStatisticService;

    @PostMapping("/overview")
    public ResponseEntity<List<ProcessOverviewDto>> getGroupOverview(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        List<ProcessOverviewDto> overview = processStatisticService.getProcessOverview(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(overview);
    }

    @PostMapping("/statistic")
    public ResponseEntity<DrawingCodeProcessStatistic> getGroupStatistic(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        DrawingCodeProcessStatistic statistic = processStatisticService.getStatisticsForProcess(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(statistic);
    }
}
