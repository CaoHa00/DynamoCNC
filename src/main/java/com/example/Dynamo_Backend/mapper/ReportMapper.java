package com.example.Dynamo_Backend.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.ReportDto;
import com.example.Dynamo_Backend.entities.Report;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class ReportMapper {
    public static ReportDto maptoReportDto(Report report) {
        ReportDto reportDto = new ReportDto();
        reportDto.setDateTime(DateTimeUtil.convertTimestampToStringDate(report.getDateTime()));
        reportDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(report.getCreatedDate()));
        reportDto.setId(report.getId());
        reportDto.setGroupId(report.getGroup().getGroupId());
        reportDto.setAdminId(report.getAdmin().getId());
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
