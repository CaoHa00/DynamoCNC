package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.StaffKpiDto;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.mapper.StaffKpiMapper;
import com.example.Dynamo_Backend.repository.StaffKpiRepository;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.service.StaffKpiService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StaffKpiImplementation implements StaffKpiService {
    StaffKpiRepository staffKpiRepository;
    StaffRepository staffRepository;

    @Override
    public StaffKpiDto addStaffKpi(StaffKpiDto staffKpiDto) {
        long createdTimestamp = System.currentTimeMillis();
        Staff staff = staffRepository.findById(staffKpiDto.getStaffId())
                .orElseThrow(() -> new RuntimeException("StaffKpiKpi is not found:" + staffKpiDto.getStaffId()));
        StaffKpi staffKpi = StaffKpiMapper.mapToStaffKpi(staffKpiDto);
        staffKpi.setStaff(staff);
        staffKpi.setCreatedDate(createdTimestamp);
        staffKpi.setUpdatedDate(createdTimestamp);
        StaffKpi saveStaffKpi = staffKpiRepository.save(staffKpi);
        return StaffKpiMapper.mapToStaffKpiDto(saveStaffKpi);
    }

    @Override
    public StaffKpiDto updateStaffKpi(Integer Id, StaffKpiDto staffKpiDto) {
        StaffKpi staffKpi = staffKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("StaffKpi is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        String a = staffKpiDto.getStaffId();
        Staff staff = staffRepository.findById(staffKpiDto.getStaffId())
                .orElseThrow(() -> new RuntimeException("StaffKpi is not found:" + staffKpiDto.getStaffId()));
        staffKpi.setStaff(staff);
        staffKpi.setYear(staffKpiDto.getYear());
        staffKpi.setMonth(staffKpiDto.getMonth());
        staffKpi.setPgTimeGoal(staffKpiDto.getPgTimeGoal());
        staffKpi.setKpi(staffKpiDto.getKpi());
        staffKpi.setOleGoal(staffKpiDto.getOleGoal());
        staffKpi.setWorkGoal(staffKpiDto.getWorkGoal());
        staffKpi.setMachineTimeGoal(staffKpiDto.getMachineTimeGoal());
        staffKpi.setManufacturingPoint(staffKpiDto.getManufacturingPoint());
        staffKpi.setUpdatedDate(updatedTimestamp);
        StaffKpi saveStaffKpi = staffKpiRepository.save(staffKpi);
        return StaffKpiMapper.mapToStaffKpiDto(saveStaffKpi);
    }

    @Override
    public StaffKpiDto updateStaffKpiByStaffId(String staffId, StaffKpiDto staffKpiDto) {
        long updatedTimestamp = System.currentTimeMillis();
        StaffKpi staffKpi = staffKpiRepository.findByStaff_Id(staffId).stream()
                .filter(kpi -> staffKpiDto.getMonth() == kpi.getMonth() &&
                        staffKpiDto.getYear() == kpi.getYear())
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No staffKpi found for staff ID: " + staffId));// orElse add new StaffKpi

        // staffKpi.setYear(staffKpiDto.getYear());
        // staffKpi.setMonth(staffKpiDto.getMonth());
        staffKpi.setPgTimeGoal(staffKpiDto.getPgTimeGoal());
        staffKpi.setKpi(staffKpiDto.getKpi());
        staffKpi.setOleGoal(staffKpiDto.getOleGoal());
        staffKpi.setWorkGoal(staffKpiDto.getWorkGoal());
        staffKpi.setMachineTimeGoal(staffKpiDto.getMachineTimeGoal());
        staffKpi.setManufacturingPoint(staffKpiDto.getManufacturingPoint());
        if (staffKpiDto.getCreatedDate() == null) {
            staffKpi.setCreatedDate(staffKpi.getCreatedDate());
        }
        staffKpi.setUpdatedDate(updatedTimestamp);
        StaffKpi saveStaffKpi = staffKpiRepository.save(staffKpi);
        return StaffKpiMapper.mapToStaffKpiDto(saveStaffKpi);
    }

    @Override
    public StaffKpiDto getStaffKpiById(Integer Id) {
        StaffKpi staffKpi = staffKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("StaffKpi is not found:" + Id));
        return StaffKpiMapper.mapToStaffKpiDto(staffKpi);
    }

    @Override
    public void deleteStaffKpi(Integer Id) {
        StaffKpi staffKpi = staffKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("StaffKpi is not found:" + Id));
        staffKpiRepository.delete(staffKpi);
    }

    @Override
    public List<StaffKpiDto> getStaffKpis() {
        List<StaffKpi> staffKpis = staffKpiRepository.findAll();
        return staffKpis.stream().map(StaffKpiMapper::mapToStaffKpiDto).toList();
    }

    @Override
    public void importStaffKpiFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<StaffKpi> staffKpiList = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue; // Skip header row
                StaffKpi staffKpi = new StaffKpi();
                staffKpi.setStaff(staffRepository.findByStaffId((int) row.getCell(0).getNumericCellValue())
                        .orElseThrow(() -> new RuntimeException(
                                "Staff not found for ID: " + row.getCell(0).getStringCellValue())));
                staffKpi.setYear((int) row.getCell(1).getNumericCellValue());
                staffKpi.setMonth((int) row.getCell(2).getNumericCellValue());
                staffKpi.setManufacturingPoint((float) row.getCell(3).getNumericCellValue());
                staffKpi.setPgTimeGoal((float) row.getCell(4).getNumericCellValue());
                staffKpi.setMachineTimeGoal((float) row.getCell(5).getNumericCellValue());
                staffKpi.setWorkGoal((float) row.getCell(6).getNumericCellValue());

                staffKpi.setKpi((float) row.getCell(7).getNumericCellValue());
                staffKpi.setOleGoal((float) row.getCell(8).getNumericCellValue());
                long currentTimestamp = System.currentTimeMillis();
                staffKpi.setCreatedDate(currentTimestamp);
                staffKpi.setUpdatedDate(currentTimestamp);
                staffKpiList.add(staffKpi);
            }
            staffKpiRepository.saveAll(staffKpiList);
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to import staff KPI from Excel file: " + e.getMessage(), e);
        }
    }

}
