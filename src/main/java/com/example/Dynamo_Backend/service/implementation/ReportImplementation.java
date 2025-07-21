package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.ReportDto;
import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Report;
import com.example.Dynamo_Backend.mapper.ReportMapper;
import com.example.Dynamo_Backend.repository.*;
import com.example.Dynamo_Backend.service.ReportService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ReportImplementation implements ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    AdminRepository adminRepository;

    @Override
    public ReportDto addReport(ReportDto reportDto) {
        Report report = ReportMapper.mapToReport(reportDto);
        long createdTimestamp = System.currentTimeMillis();
        Group group = groupRepository.findById(reportDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group is not found:" + reportDto.getGroupId()));
        report.setGroup(group);

        Admin admin = adminRepository.findById(reportDto.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin is not found:" +
                        reportDto.getAdminId()));
        report.setAdmin(admin);

        report.setCreatedDate(createdTimestamp);

        Report savedReport = reportRepository.save(report);
        return ReportMapper.maptoReportDto(savedReport);
    }

    @Override
    public ReportDto updateReport(Integer reportId, ReportDto reportDto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report is not found:" + reportId));
        Group group = groupRepository.findById(reportDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group is not found:" + reportDto.getGroupId()));
        Admin admin = adminRepository.findById(reportDto.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin is not found:" +
                        reportDto.getAdminId()));
        report.setAdmin(admin);
        report.setGroup(group);
        report.setHourDiff(reportDto.getHourDiff());
        report.setOffice(reportDto.getOffice());
        report.setReportType(reportDto.getReportType());
        Report updatedReport = reportRepository.save(report);
        return ReportMapper.maptoReportDto(updatedReport);
    }

    @Override
    public ReportDto getReportById(Integer reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report is not found:" + reportId));
        return ReportMapper.maptoReportDto(report);
    }

    @Override
    public void deleteReport(Integer reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report is not found:" + reportId));
        reportRepository.delete(report);
    }

    @Override
    public List<ReportDto> getAllReport() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream().map(ReportMapper::maptoReportDto).toList();
    }

}
