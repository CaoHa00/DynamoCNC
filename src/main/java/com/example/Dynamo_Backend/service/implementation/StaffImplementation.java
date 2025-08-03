package com.example.Dynamo_Backend.service.implementation;

import java.util.ArrayList;
import java.util.List;

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
        Staff staff = StaffMapper.mapToEntity(staffRequestDto);
        long createdTimestamp = System.currentTimeMillis();
        Group group = groupRepository.findById(staffRequestDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group is not found:" + staffRequestDto.getGroupId()));

        staff.setGroup(group);
        staff.setCreatedDate(createdTimestamp);
        staff.setUpdatedDate(createdTimestamp);
        staff.setStaffKpis(new ArrayList<StaffKpi>());
        Staff saveStaff = staffRepository.save(staff);

        staffRequestDto.setId(saveStaff.getId());
        StaffKpiDto staffKpiDto = StaffKpiMapper.mapToStaffKpiDto(staffRequestDto);
        StaffKpiDto saveKpi = staffKpiService.addStaffKpi(staffKpiDto);
        StaffKpi staffKpi = staffKpiRepository.findById(saveKpi.getId())
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
        staff.setStaffName(staffDto.getStaffName());
        staff.setStaffId(staffDto.getStaffId());
        staff.setStaffOffice(staffDto.getStaffOffice());
        staff.setStaffSection(staffDto.getStaffSection());
        staff.setShortName(staffDto.getShortName());
        staff.setStatus(staffDto.getStatus());
        staff.setUpdatedDate(updatedTimestamp);
        Group group = groupRepository.findById(staffDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group is not found:" + staffDto.getGroupId()));
        staff.setGroup(group);
        Staff updatedStaff = staffRepository.save(staff);
        return StaffMapper.mapToStaffDto(updatedStaff);
    }
}
