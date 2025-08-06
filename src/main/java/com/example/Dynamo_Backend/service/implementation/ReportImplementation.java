package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
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
                                .orElseThrow(() -> new RuntimeException(
                                                "Group is not found:" + reportDto.getGroupId()));
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
                                .orElseThrow(() -> new RuntimeException(
                                                "Group is not found:" + reportDto.getGroupId()));
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

        @Override
        public void importReportFromExcel(MultipartFile file) {
                try {
                        InputStream inputStream = ((MultipartFile) file).getInputStream();
                        Workbook workbook = new XSSFWorkbook(inputStream);
                        Sheet sheet = workbook.getSheetAt(0);
                        for (Row row : sheet) {
                                if (row.getRowNum() == 0)
                                        continue;
                                Report report = new Report();
                                report.setReportType(row.getCell(1).getStringCellValue());
                                report.setHourDiff((int) row.getCell(2).getNumericCellValue());
                                report.setOffice(row.getCell(3).getStringCellValue());

                                Group group = groupRepository.findByGroupName(row.getCell(4).getStringCellValue())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Group is not found when add report by excel:"
                                                                                + row.getCell(4).getStringCellValue()));
                                report.setGroup(group);
                                // sau login thi check authenticated de update admin
                                Admin admin = adminRepository.findById(row.getCell(5).getStringCellValue())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Admin is not found when add report by excel:"
                                                                                + row.getCell(5).getStringCellValue()));
                                report.setAdmin(admin);

                                long createdTimestamp = System.currentTimeMillis();
                                report.setCreatedDate(createdTimestamp);

                                reportRepository.save(report);
                        }
                        workbook.close();
                        inputStream.close();
                } catch (Exception e) {
                        throw new RuntimeException("Failed to import report from Excel file: " + e.getMessage());
                }
        }

}
