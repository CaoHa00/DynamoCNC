package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.ReportDto;
import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Report;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
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
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Group is not found:" + reportDto.getGroupId()));
                report.setGroup(group);

                Admin admin = adminRepository.findById(reportDto.getAdminId())
                                .orElseThrow(() -> new ResourceNotFoundException("Admin is not found:" +
                                                reportDto.getAdminId()));
                report.setAdmin(admin);

                report.setCreatedDate(createdTimestamp);

                Report savedReport = reportRepository.save(report);
                return ReportMapper.maptoReportDto(savedReport);
        }

        @Override
        public ReportDto updateReport(Integer reportId, ReportDto reportDto) {
                Report report = reportRepository.findById(reportId)
                                .orElseThrow(() -> new ResourceNotFoundException("Report is not found:" + reportId));
                Group group = groupRepository.findById(reportDto.getGroupId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Group is not found:" + reportDto.getGroupId()));
                Admin admin = adminRepository.findById(reportDto.getAdminId())
                                .orElseThrow(() -> new ResourceNotFoundException("Admin is not found:" +
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
                                .orElseThrow(() -> new ResourceNotFoundException("Report is not found:" + reportId));
                return ReportMapper.maptoReportDto(report);
        }

        @Override
        public void deleteReport(Integer reportId) {
                Report report = reportRepository.findById(reportId)
                                .orElseThrow(() -> new ResourceNotFoundException("Report is not found:" + reportId));
                reportRepository.delete(report);
        }

        @Override
        public List<ReportDto> getAllReport() {
                List<Report> reports = reportRepository.findAll();
                return reports.stream().map(ReportMapper::maptoReportDto).toList();
        }

        @Override
        public void importReportFromExcel(MultipartFile file) {
                try (InputStream inputStream = file.getInputStream();
                                Workbook workbook = new XSSFWorkbook(inputStream)) {
                        Sheet sheet = workbook.getSheetAt(0);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        List<Report> reports = new ArrayList<>();
                        for (Row row : sheet) {
                                if (row.getRowNum() < 6)
                                        continue; // Skip header rows

                                Report report = new Report();

                                String dateStr = row.getCell(2).getStringCellValue();
                                long dateTime = java.time.LocalDate.parse(dateStr, formatter)
                                                .atStartOfDay(java.time.ZoneId.systemDefault())
                                                .toInstant().toEpochMilli();
                                report.setDateTime(dateTime);

                                report.setOffice(row.getCell(3).getStringCellValue());

                                String groupName = row.getCell(4).getStringCellValue();
                                Group group = groupRepository.findByGroupName(groupName)
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                "Group is not found when add report by excel: "
                                                                                + groupName));
                                report.setGroup(group);

                                report.setReportType(row.getCell(5).getStringCellValue());

                                report.setHourDiff((int) row.getCell(6).getNumericCellValue());

                                String adminId = row.getCell(7).getStringCellValue();

                                // maybe find by email
                                Admin admin = adminRepository.findById(adminId)
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                "Admin is not found when add report by excel: "
                                                                                + adminId));
                                report.setAdmin(admin);

                                String createdDateStr = row.getCell(8).getStringCellValue();
                                long createdDate = java.time.LocalDate.parse(createdDateStr, formatter)
                                                .atStartOfDay(java.time.ZoneId.systemDefault())
                                                .toInstant().toEpochMilli();
                                report.setCreatedDate(createdDate);

                                reports.add(report);
                        }
                        reportRepository.saveAll(reports);
                } catch (Exception e) {
                        throw new BusinessException(
                                        "Failed to import report from Excel file: " + e.getMessage());
                }
        }
}
