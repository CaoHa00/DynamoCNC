package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupStatisticDto;
import com.example.Dynamo_Backend.service.StaffGroupStatisticService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/staff-group-statistic")
public class StaffGroupStatisticController {
    private final StaffGroupStatisticService staffGroupStatisticService;

    @GetMapping("/overview")
    public ResponseEntity<List<StaffGroupOverviewDto>> getStaffGroupOverview(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        List<StaffGroupOverviewDto> overview = staffGroupStatisticService.getStaffGroupOverview(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(overview);
    }

    @GetMapping("/statistic")
    public ResponseEntity<StaffGroupStatisticDto> getStaffGroupStatistic(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        StaffGroupStatisticDto statistic = staffGroupStatisticService.getStaffGroupStatistic(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(statistic);
    }
}
