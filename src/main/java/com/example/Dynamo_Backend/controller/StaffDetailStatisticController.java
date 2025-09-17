package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.HistoryProcessDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffDetailStatisticDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffWorkingStatisticDto;
import com.example.Dynamo_Backend.service.StaffDetailStatisticService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/staff-detail")
public class StaffDetailStatisticController {
    private final StaffDetailStatisticService staffDetailStatisticService;

    @PostMapping("/statistic")
    public ResponseEntity<StaffDetailStatisticDto> getStaffDetailStatistic(
            @RequestBody StatisticRequestDto requestDto) {
        StaffDetailStatisticDto statisticDto = staffDetailStatisticService.getStaffDetailStatistic(requestDto);
        return ResponseEntity.ok(statisticDto);
    }

    @PostMapping("/history")
    public ResponseEntity<List<HistoryProcessDto>> getStaffHistoryProcess(
            @RequestBody StatisticRequestDto requestDto) {
        List<HistoryProcessDto> historyProcessDtos = staffDetailStatisticService.getStaffHistoryProcesses(requestDto);
        return ResponseEntity.ok(historyProcessDtos);
    }

    @PostMapping("/working-statistic")
    public ResponseEntity<StaffWorkingStatisticDto> getStaffWorkingStatistic(
            @RequestBody StatisticRequestDto requestDto) {
        StaffWorkingStatisticDto workingStatisticDto = staffDetailStatisticService.getStaffWorkingStatistic(requestDto);
        return ResponseEntity.ok(workingStatisticDto);
    }

    @PostMapping("/detail")
    public ResponseEntity<StaffWorkingStatisticDto> getStaffWorkingStatisticDetail(
            @RequestBody StatisticRequestDto requestDto) {
        StaffWorkingStatisticDto workingStatisticDto = staffDetailStatisticService.getStaffWorkingStatistic(requestDto);
        return ResponseEntity.ok(workingStatisticDto);
    }

}
