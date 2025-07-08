package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.mapper.StaffMapper;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.service.StaffService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StaffImplementation implements StaffService {
    @Autowired
    StaffRepository staffRepository;

    @Override
    public StaffDto addStaff(StaffDto staffDto) {
        Staff staff = StaffMapper.mapToStaff(staffDto);
        long createdTimestamp = System.currentTimeMillis();
        int status = 1;
        staff.setCreatedDate(createdTimestamp);
        staff.setUpdatedDate(createdTimestamp);
        staff.setStatus(status);
        Staff savStaff = staffRepository.save(staff);
        return StaffMapper.mapToStaffDto(savStaff);
    }

    @Override
    public void deleteStaff(List<String> ids) {
        for (String id : ids) {
            Staff staff = staffRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Staff is not found:" + id));
            staffRepository.delete(staff);
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
    public StaffDto updateStaff(String Id, StaffDto staffDto) {
        Staff staff = staffRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + Id));

        long updatedTimestamp = System.currentTimeMillis();
        staff.setStaffName(staffDto.getStaffName());
        staff.setStaffId(staffDto.getStaffId());
        staff.setStaffOffice(staffDto.getStaffOffice());
        staff.setStaffSection(staffDto.getStaffSection());
        staff.setStaffStep(staffDto.getStaffStep());
        staff.setKpi(staffDto.getKpi());
        staff.setStatus(staffDto.getStatus());
        staff.setUpdatedDate(updatedTimestamp);

        Staff updatedStaff = staffRepository.save(staff);
        return StaffMapper.mapToStaffDto(updatedStaff);
    }
}
