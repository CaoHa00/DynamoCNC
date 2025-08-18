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
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.mapper.StaffKpiMapper;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.StaffKpiRepository;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.service.StaffKpiService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StaffKpiImplementation implements StaffKpiService {
    StaffKpiRepository staffKpiRepository;
    StaffRepository staffRepository;
    GroupRepository groupRepository;

    @Override
    public StaffKpiDto addStaffKpi(StaffKpiDto staffKpiDto) {
        StaffKpi staffKpi = staffKpiRepository.findByStaff_IdAndMonthAndYear(staffKpiDto.getStaffId(),
                staffKpiDto.getMonth(),
                staffKpiDto.getYear());
        if (staffKpi != null) {
            throw new IllegalArgumentException("Goal of this staff is already set");
        }
        long createdTimestamp = System.currentTimeMillis();
        Staff staff = staffRepository.findById(staffKpiDto.getStaffId())
                .orElseThrow(() -> new RuntimeException("StaffKpiKpi is not found:" + staffKpiDto.getStaffId()));
        staffKpi = StaffKpiMapper.mapToStaffKpi(staffKpiDto);
        Group group = groupRepository.findById(staffKpiDto.getGroupId()).orElse(null);
        staffKpi.setGroup(group);
        staffKpi.setStaff(staff);
        staffKpi.setCreatedDate(createdTimestamp);
        staffKpi.setUpdatedDate(createdTimestamp);
        StaffKpi saveStaffKpi = staffKpiRepository.save(staffKpi);
        return StaffKpiMapper.mapToStaffKpiDto(saveStaffKpi);
    }

    @Override
    public StaffKpiDto updateStaffKpi(Integer id, StaffKpiDto dto) {
        // Try to find an existing KPI by staff + month + year
        StaffKpi staffKpi = staffKpiRepository.findByStaff_IdAndMonthAndYear(
                dto.getStaffId(), dto.getMonth(), dto.getYear());

        // If it exists and is not the same record, reject
        if (staffKpi != null && !staffKpi.getId().equals(id)) {
            throw new IllegalArgumentException("Goal of this staff is already set");
        }

        // If it exists and is identical, reject
        if (staffKpi != null && staffKpi.isSameAs(dto)) {
            throw new IllegalArgumentException("Goal of this staff is already set");
        }

        // Load the record by ID if not found earlier
        if (staffKpi == null) {
            staffKpi = staffKpiRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("StaffKpi not found with id: " + id));
        }

        // Set related entities
        Group group = groupRepository.findById(dto.getGroupId()).orElse(null);
        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found: " + dto.getStaffId()));

        // Update fields
        staffKpi.setGroup(group);
        staffKpi.setStaff(staff);
        staffKpi.setYear(dto.getYear());
        staffKpi.setMonth(dto.getMonth());
        staffKpi.setPgTimeGoal(dto.getPgTimeGoal());
        staffKpi.setKpi(dto.getKpi());
        staffKpi.setOleGoal(dto.getOleGoal());
        staffKpi.setWorkGoal(dto.getWorkGoal());
        staffKpi.setMachineTimeGoal(dto.getMachineTimeGoal());
        staffKpi.setManufacturingPoint(dto.getManufacturingPoint());
        staffKpi.setUpdatedDate(System.currentTimeMillis());

        // Save & return
        StaffKpi saved = staffKpiRepository.save(staffKpi);
        return StaffKpiMapper.mapToStaffKpiDto(saved);
    }

    @Override
    public StaffKpiDto updateStaffKpiByStaffId(String staffId, StaffKpiDto staffKpiDto) {
        long updatedTimestamp = System.currentTimeMillis();
        StaffKpi staffKpi = staffKpiRepository.findByStaff_IdAndMonthAndYear(staffId,
                staffKpiDto.getMonth(),
                staffKpiDto.getYear());
        if (staffKpi.isSameAs(staffKpiDto)) {
            throw new IllegalArgumentException("Goal of this staff is already set");
        } else {
            Group group = groupRepository.findById(staffKpiDto.getGroupId()).orElse(null);
            staffKpi.setGroup(group);
            staffKpi.setYear(staffKpiDto.getYear());
            staffKpi.setMonth(staffKpiDto.getMonth());
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
