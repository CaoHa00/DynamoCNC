package com.example.Dynamo_Backend.controller;

import com.example.Dynamo_Backend.dto.ProcessTimeSummaryDto;
import com.example.Dynamo_Backend.service.ProcessTimeSummaryService;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/process-time-summary")
@AllArgsConstructor
public class ProcessTimeSummaryController {

    private final ProcessTimeSummaryService service;

    @GetMapping
    public ResponseEntity<List<ProcessTimeSummaryDto>> getAll() {
        List<ProcessTimeSummaryDto> summaries = service.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(summaries);
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<ProcessTimeSummaryDto> getByOrderCode(@PathVariable String orderCode) {
        ProcessTimeSummaryDto summary = service.getByOrderCode(orderCode);
        return ResponseEntity.status(HttpStatus.OK).body(summary);
    }

    @PostMapping("/sum/{orderCode}")
    public ResponseEntity<ProcessTimeSummaryDto> sumTimesByOrderCode(@PathVariable String orderCode) {
        ProcessTimeSummaryDto processTimeSummaryDto = service.sumTimesByOrderCode(orderCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(processTimeSummaryDto);
    }
}
