package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.StaffKpiDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
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
            throw new BusinessException("Goal of this staff is already set");
        }
        long createdTimestamp = System.currentTimeMillis();
        Staff staff = staffRepository.findById(staffKpiDto.getStaffId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("StaffKpi is not found:" + staffKpiDto.getStaffId()));
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
            throw new BusinessException("Goal of this staff is already set");
        }

        // If it exists and is identical, reject
        if (staffKpi != null && staffKpi.isSameAs(dto)) {
            throw new BusinessException("Goal of this staff is already set");
        }

        // Load the record by ID if not found earlier
        if (staffKpi == null) {
            staffKpi = staffKpiRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("StaffKpi not found with id: " + id));
        }

        // Set related entities
        Group group = groupRepository.findById(dto.getGroupId()).orElse(null);
        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + dto.getStaffId()));

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
            throw new BusinessException("Goal of this staff is already set");
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
                .orElseThrow(() -> new ResourceNotFoundException("StaffKpi is not found:" + Id));
        return StaffKpiMapper.mapToStaffKpiDto(staffKpi);
    }

    @Override
    public void deleteStaffKpi(Integer Id) {
        StaffKpi staffKpi = staffKpiRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffKpi is not found:" + Id));
        staffKpiRepository.delete(staffKpi);
    }

    @Override
    public List<StaffKpiDto> getStaffKpis() {
        List<StaffKpi> staffKpis = staffKpiRepository.findAll();
        return staffKpis.stream().map(StaffKpiMapper::mapToStaffKpiDto).toList();
    }

    @Override
    public void importStaffKpiFromExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<StaffKpi> staffKpis = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() < 6)
                    continue; // Skip header row

                boolean missing = false;
                for (int i = 2; i <= 11; i++) {
                    if (row.getCell(i) == null) {
                        missing = true;
                        break;
                    }
                }
                if (missing)
                    continue;

                StaffKpi staffKpi = new StaffKpi();
                staffKpi.setYear((int) row.getCell(2).getNumericCellValue());
                staffKpi.setMonth((int) row.getCell(3).getNumericCellValue());

                int staffId = (int) row.getCell(4).getNumericCellValue();
                Staff staff = staffRepository.findByStaffId(staffId)
                        .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffId));
                staffKpi.setStaff(staff);

                String groupName = row.getCell(5).getStringCellValue();
                Group group = groupRepository.findByGroupName(groupName)
                        .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + groupName));
                staffKpi.setGroup(group);

                staffKpi.setPgTimeGoal((float) row.getCell(6).getNumericCellValue());
                staffKpi.setManufacturingPoint((float) row.getCell(7).getNumericCellValue());
                staffKpi.setMachineTimeGoal((float) row.getCell(8).getNumericCellValue());
                staffKpi.setWorkGoal((float) row.getCell(9).getNumericCellValue());
                staffKpi.setKpi((float) row.getCell(10).getNumericCellValue());
                staffKpi.setOleGoal((float) row.getCell(11).getNumericCellValue());

                long currentTimestamp = System.currentTimeMillis();
                staffKpi.setCreatedDate(currentTimestamp);
                staffKpi.setUpdatedDate(currentTimestamp);
                staffKpis.add(staffKpi);
            }
            staffKpiRepository.saveAll(staffKpis);
        } catch (Exception e) {
            throw new BusinessException("Failed to import staff KPI from Excel file: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 1 * ?") // Runs at 12:00 AM on the 1st of every month
    public void createMonthlyStaffKpis() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        // Previous month
        int prevYear = (month == 1) ? year - 1 : year;
        int prevMonth = (month == 1) ? 12 : month - 1;

        List<Staff> activeStaff = staffRepository.findAllByStatus(1);
        int createdCount = 0;

        for (Staff staff : activeStaff) {
            // Check if KPI already exists for current month
            StaffKpi existing = staffKpiRepository.findByStaff_IdAndMonthAndYear(staff.getId(), month, year);
            if (existing != null) {
                continue; // Skip if already exists
            }

            // Fetch previous month's KPI
            StaffKpi prevKpi = staffKpiRepository.findByStaff_IdAndMonthAndYear(staff.getId(), prevMonth, prevYear);

            StaffKpiDto dto = new StaffKpiDto();
            dto.setStaffId(staff.getId());
            dto.setYear(year);
            dto.setMonth(month);

            if (prevKpi != null) {
                dto.setPgTimeGoal(prevKpi.getPgTimeGoal());
                dto.setMachineTimeGoal(prevKpi.getMachineTimeGoal());
                dto.setManufacturingPoint(prevKpi.getManufacturingPoint());
                dto.setOleGoal(prevKpi.getOleGoal());
                dto.setWorkGoal(prevKpi.getWorkGoal());
                dto.setKpi(prevKpi.getKpi());
                dto.setGroupId(prevKpi.getGroup().getGroupId());
            } else {
                List<Group> group = groupRepository.findAll();
                dto.setPgTimeGoal(0.0f);
                dto.setMachineTimeGoal(0.0f);
                dto.setManufacturingPoint(0.0f);
                dto.setOleGoal(0.0f);
                dto.setWorkGoal(0.0f);
                dto.setKpi(0.0f);
                dto.setGroupId(group.get(0).getGroupId());
            }

            try {
                addStaffKpi(dto);
                createdCount++;
            } catch (BusinessException e) {
                // Already exists or other business logic, skip
            }
        }

        System.out.println("Monthly Staff KPI creation completed. Created " + createdCount + " new KPIs for " + year
                + "-" + month);
    }
}
