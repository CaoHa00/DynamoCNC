package com.example.Dynamo_Backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.ReportDto;

public interface ReportService {
    ReportDto addReport(ReportDto reportDto);

    ReportDto updateReport(Integer reportId, ReportDto reportDto);

    ReportDto getReportById(Integer reportId);

    void deleteReport(Integer reportId);

    List<ReportDto> getAllReport();

    void importReportFromExcel(MultipartFile file);

}
