package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.*;
import com.example.Dynamo_Backend.dto.RequestDto.StaffRequestDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.mapper.StaffKpiMapper;
import com.example.Dynamo_Backend.mapper.StaffMapper;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.StaffKpiRepository;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.service.StaffKpiService;
import com.example.Dynamo_Backend.service.StaffService;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class StaffImplementation implements StaffService {
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    GroupRepository groupRepository;

    StaffKpiService staffKpiService;
    StaffKpiRepository staffKpiRepository;

    @Override
    public StaffDto addStaff(StaffRequestDto staffRequestDto) {

        Optional<Staff> existingStaff = staffRepository.findByStaffId(staffRequestDto.getStaffId());
        if (existingStaff.isPresent()) {
            throw new IllegalArgumentException("Staff ID already exists");
        }
        Staff staff = StaffMapper.mapToEntity(staffRequestDto);
        long createdTimestamp = System.currentTimeMillis();

        staff.setStatus(1);
        staff.setCreatedDate(createdTimestamp);
        staff.setUpdatedDate(createdTimestamp);
        staff.setStaffKpis(new ArrayList<StaffKpi>());
        Staff saveStaff = staffRepository.save(staff);

        staffRequestDto.setId(saveStaff.getId());
        StaffKpiDto staffKpiDto = StaffKpiMapper.mapToStaffKpiDto(staffRequestDto);
        StaffKpiDto saveKpi = staffKpiService.addStaffKpi(staffKpiDto);
        StaffKpi staffKpi = staffKpiRepository.findById(saveKpi.getKpiId())
                .orElseThrow(() -> new RuntimeException("StaffKpi is not found:"));
        saveStaff.getStaffKpis().add(staffKpi);

        return StaffMapper.mapToStaffDto(saveStaff);
    }

    @Override
    public void deleteStaff(List<String> ids) {
        for (String id : ids) {
            Staff staff = staffRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Staff is not found:" + id));
            staffRepository.delete(staff);

            List<StaffKpi> staffKpis = staffKpiRepository.findByStaff_Id(id);
            for (StaffKpi staffKpi : staffKpis) {
                staffKpiRepository.delete(staffKpi);
            }

        }
    }

    @Override
    public List<StaffDto> getAllStaffs() {
        List<Staff> staffs = staffRepository.findAll();
        return staffs.stream().map(StaffMapper::mapToStaffDto).toList();
    }

    @Override
    public StaffDto getStaffById(String Id) {
        Staff staff = staffRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + Id));
        return StaffMapper.mapToStaffDto(staff);
    }

    @Override
    public StaffDto updateStaff(String Id, StaffRequestDto staffDto) {
        Staff staff = staffRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + Id));

        long updatedTimestamp = System.currentTimeMillis();
        staff.setCreatedDate(staff.getCreatedDate());
        staff.setStaffName(staffDto.getStaffName());
        staff.setStaffId(staffDto.getStaffId());
        staff.setStaffOffice(staffDto.getStaffOffice());
        staff.setStaffSection(staffDto.getStaffSection());
        staff.setShortName(staffDto.getShortName());
        staff.setStatus(staffDto.getStatus() != null ? staffDto.getStatus() : staff.getStatus());
        staff.setUpdatedDate(updatedTimestamp);
        StaffKpiDto staffKpiDto = StaffKpiMapper.mapToStaffKpiDto(staffDto);
        StaffKpi existingKpi = staffKpiRepository.findByStaff_IdAndMonthAndYear(
                staff.getId(),
                staffKpiDto.getMonth(),
                staffKpiDto.getYear());
        if (!existingKpi.isSameAs(staffKpiDto)) {
            staffKpiService.updateStaffKpiByStaffId(staff.getId(), staffKpiDto);
        }
        Staff updatedStaff = staffRepository.save(staff);
        return StaffMapper.mapToStaffDto(updatedStaff);
    }

    @Override
    public void importStaffFromExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<StaffKpi> staffKpiList = new ArrayList<>();

            java.time.LocalDate now = java.time.LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();

            for (Row row : sheet) {
                if (row.getRowNum() < 2)
                    continue; // Skip header rows

                Staff staff = new Staff();
                staff.setStaffId(Integer.parseInt(row.getCell(0).toString().trim()));
                staff.setStaffName(row.getCell(1).getStringCellValue());
                staff.setShortName(row.getCell(2).getStringCellValue());
                staff.setStaffOffice(row.getCell(3).getStringCellValue());
                staff.setStaffSection(row.getCell(4).getStringCellValue());
                staff.setStatus(1);

                // Save staff first to get ID for KPI
                Staff savedStaff = staffRepository.save(staff);

                // Create StaffKpi for current month
                StaffKpi staffKpi = new StaffKpi();
                // Set group
                String groupName = row.getCell(5).getStringCellValue();
                Group group = groupRepository.findByGroupName(groupName)
                        .orElseThrow(() -> new RuntimeException("Group not found: " + groupName));
                staffKpi.setGroup(group);
                staffKpi.setStaff(savedStaff);
                staffKpi.setMonth(currentMonth);
                staffKpi.setYear(currentYear);
                staffKpi.setPgTimeGoal((float) row.getCell(6).getNumericCellValue());
                staffKpi.setMachineTimeGoal((float) row.getCell(7).getNumericCellValue());
                staffKpi.setWorkGoal((float) row.getCell(8).getNumericCellValue());
                staffKpi.setKpi((float) row.getCell(9).getNumericCellValue());
                staffKpi.setOleGoal((float) row.getCell(10).getNumericCellValue());
                staffKpiList.add(staffKpi);
            }

            // Save all KPIs
            staffKpiRepository.saveAll(staffKpiList);

        } catch (Exception e) {
            throw new RuntimeException("Failed to import staff from Excel file: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StaffDto> getAllStaffByStatus() {
        List<Staff> staffs = staffRepository.findAllByStatus(1);
        return staffs.stream().map(StaffMapper::mapToStaffDto).toList();
    }
}
