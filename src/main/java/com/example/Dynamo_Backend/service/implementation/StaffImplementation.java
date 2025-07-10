package com.example.Dynamo_Backend.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.*;
import com.example.Dynamo_Backend.dto.RequestDto.StaffRequestDto;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.mapper.StaffMapper;
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

    StaffKpiRepository staffKpiRepository;

    @Override
    public StaffDto addStaff(StaffRequestDto staffRequestDto) {
        Staff staff = new Staff();
        long createdTimestamp = System.currentTimeMillis();
        int status = 1;
        staff.setId(staffRequestDto.getId());
        staff.setStaffId(staffRequestDto.getStaffId());
        staff.setStaffName(staffRequestDto.getStaffName());
        staff.setStaffOffice(staffRequestDto.getStaffOffice());
        staff.setStaffSection(staffRequestDto.getStaffSection());
        staff.setStaffStep(staffRequestDto.getStaffStep());
        staff.setKpi(staffRequestDto.getKpi());
        staff.setCreatedDate(createdTimestamp);
        staff.setUpdatedDate(createdTimestamp);
        staff.setStatus(status);
        Staff savStaff = staffRepository.save(staff);
        StaffKpi staffKpi = new StaffKpi();
        staffKpi.setStaff(savStaff);
        staffKpi.setCreatedDate(createdTimestamp);
        staffKpi.setUpdatedDate(createdTimestamp);
        staffKpi.setDuration(staffRequestDto.getDuration());
        staffKpi.setYear(staffRequestDto.getYear());
        staffKpi.setMonth(staffRequestDto.getMonth());
        staffKpi.setWeek(staffRequestDto.getWeek());
        staffKpi.setPgTimeGoal(staffRequestDto.getPgTimeGoal());
        staffKpi.setKpi(staffRequestDto.getKpi());
        staffKpi.setOleGoal(staffRequestDto.getOleGoal());
        staffKpi.setMachineTimeGoal(staffRequestDto.getMachineTimeGoal());
        staffKpi.setManufacturingPoint(staffRequestDto.getManufacturingPoint());
        staffKpiRepository.save(staffKpi);
        staff.setStaffKpis(new ArrayList<StaffKpi>());
        staff.getStaffKpis().add(staffKpi);
        return StaffMapper.mapToStaffDto(savStaff);
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
    public StaffDto updateStaff(String Id, StaffDto staffRequestDto) {
        Staff staff = staffRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + Id));

        long updatedTimestamp = System.currentTimeMillis();
        staff.setStaffName(staffRequestDto.getStaffName());
        staff.setStaffId(staffRequestDto.getStaffId());
        staff.setStaffOffice(staffRequestDto.getStaffOffice());
        staff.setStaffSection(staffRequestDto.getStaffSection());
        staff.setStaffStep(staffRequestDto.getStaffStep());
        staff.setKpi(staffRequestDto.getKpi());
        staff.setStatus(staffRequestDto.getStatus());
        staff.setUpdatedDate(updatedTimestamp);

        Staff updatedStaff = staffRepository.save(staff);
        return StaffMapper.mapToStaffDto(updatedStaff);
    }
}
