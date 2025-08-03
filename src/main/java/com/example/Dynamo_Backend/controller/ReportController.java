package com.example.Dynamo_Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Dynamo_Backend.dto.ReportDto;
import com.example.Dynamo_Backend.service.ReportService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/report")
public class ReportController {
    public final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportDto> addReport(@RequestBody ReportDto reportDto) {
        ReportDto newReportDto = reportService.addReport(reportDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReportDto);
    }

    @GetMapping
    public ResponseEntity<List<ReportDto>> getAllReports() {
        List<ReportDto> reportDtos = reportService.getAllReport();
        return ResponseEntity.status(HttpStatus.OK).body(reportDtos);
    }

    @PostMapping("/{report_id}")
    public ResponseEntity<ReportDto> getReportById(@PathVariable("report_id") Integer Id) {
        ReportDto reportDto = reportService.getReportById(Id);
        return ResponseEntity.status(HttpStatus.CREATED).body(reportDto);
    }

    @PutMapping("/{report_id}")
    public ResponseEntity<ReportDto> updateReport(@PathVariable("report_id") Integer Id,
            @RequestBody ReportDto reportDto) {
        ReportDto updatedReportDto = reportService.updateReport(Id, reportDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedReportDto);
    }

    @DeleteMapping("/{report_id}")
    public ResponseEntity<Void> deleteReport(@PathVariable("report_id") Integer Id) {
        reportService.deleteReport(Id);
        return ResponseEntity.ok().build();
    }
}
