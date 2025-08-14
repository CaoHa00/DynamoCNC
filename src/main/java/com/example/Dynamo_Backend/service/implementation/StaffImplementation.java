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
        staff.setStatus(staffDto.getStatus());
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
        try {
            InputStream inputStream = ((MultipartFile) file).getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<Staff> staffList = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;
                Staff staffDto = new Staff();
                Cell staffIdCell = row.getCell(0);
                if (staffIdCell.getCellType() == CellType.NUMERIC) {
                    staffDto.setStaffId((int) staffIdCell.getNumericCellValue());
                } else {
                    staffDto.setStaffId(Integer.parseInt(staffIdCell.getStringCellValue()));
                }
                staffDto.setStaffName(row.getCell(1).getStringCellValue());
                staffDto.setShortName(row.getCell(2).getStringCellValue());
                staffDto.setStaffOffice(row.getCell(3).getStringCellValue());
                Group group = groupRepository.findByGroupName(row.getCell(4).getStringCellValue())
                        .orElseThrow(() -> new RuntimeException(
                                "Group is not found when add staff by excel:" + row.getCell(5).getStringCellValue()));
                staffDto.setStaffSection(row.getCell(5).getStringCellValue());
                staffDto.setStatus(1);

                staffList.add(staffDto);
            }

            for (Staff staff : staffList) {
                staffRepository.save(staff);
            }
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to import staff from Excel file: " + e.getMessage());
        }
    }

    @Override
    public List<StaffDto> getAllStaffByStatus() {
        List<Staff> staffs = staffRepository.findAllByStatus(1);
        return staffs.stream().map(StaffMapper::mapToStaffDto).toList();
    }
}
