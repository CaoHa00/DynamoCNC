package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.StatisticRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.*;
import com.example.Dynamo_Backend.service.MachineDetailStatisticService;

import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("/detail")
    public ResponseEntity<MachineEfficiencyResponseDto> getMachineEfficiencyByOtherGroup(
            @RequestBody StatisticRequestDto requestDto) {
        MachineEfficiencyResponseDto efficiency = machineDetailStatisticService.getMachineEfficiency(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(efficiency);
    }

    @PostMapping("/export-excel")
    public void exportExcel(@RequestBody StatisticRequestDto requestDto,
            HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename");
            machineDetailStatisticService.exportExcelToResponse(requestDto, response);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }
}
