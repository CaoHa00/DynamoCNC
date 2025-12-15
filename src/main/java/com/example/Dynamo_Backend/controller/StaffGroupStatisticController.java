package com.example.Dynamo_Backend.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.StaffGroupStatisticDto;
import com.example.Dynamo_Backend.service.GroupStatisticService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/staff-group-statistic")
public class StaffGroupStatisticController {
    private final GroupStatisticService groupStatisticService;

    @PostMapping("/overview")
    public ResponseEntity<List<StaffGroupOverviewDto>> getGroupOverview(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        List<StaffGroupOverviewDto> overview = groupStatisticService.getGroupOverview(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(overview);
    }

    @PostMapping("/statistic")
    public ResponseEntity<StaffGroupStatisticDto> getGroupStatistic(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        StaffGroupStatisticDto statistic = groupStatisticService.getGroupStatistic(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(statistic);
    }

    @PostMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(@RequestBody GroupEfficiencyRequestDto requestDto)
            throws java.io.IOException {
        ByteArrayInputStream in = groupStatisticService.exportExcel(requestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());
    }
}
