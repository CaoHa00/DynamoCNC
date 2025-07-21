package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.ReportDto;
import com.example.Dynamo_Backend.entities.Report;

public class ReportMapper {
    public static ReportDto maptoReportDto(Report report) {
        ReportDto reportDto = new ReportDto();
        String formattedCreatedDate = Instant.ofEpochMilli(report.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String date = Instant.ofEpochMilli(report.getDateTime())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        reportDto.setDateTime(date);
        reportDto.setCreatedDate(formattedCreatedDate);
        reportDto.setId(report.getId());
        reportDto.setGroupId(report.getGroup().getGroupId());
        // reportDto.setAdminId(report.getAdmin().getId());
        reportDto.setHourDiff(report.getHourDiff());
        reportDto.setOffice(report.getOffice());
        reportDto.setReportType(report.getReportType());

        return reportDto;
    }

    public static Report mapToReport(ReportDto reportDto) {
        Report report = new Report();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(reportDto.getDateTime(), formatter);
        long timestamp = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        report.setDateTime(timestamp);
        report.setHourDiff(reportDto.getHourDiff());
        report.setId(reportDto.getId());
        report.setOffice(reportDto.getOffice());
        report.setReportType(reportDto.getReportType());

        return report;
    }
}
