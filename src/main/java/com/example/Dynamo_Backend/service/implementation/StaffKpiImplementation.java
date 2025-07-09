package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + staffKpiDto.getStaffId()));
        StaffKpi staffKpi = StaffKpiMapper.mapToStaffKpi(staffKpiDto);
        staffKpi.setStaff(staff);
        staffKpi.setCreatedDate(createdTimestamp);
        StaffKpi saveStaffKpi = staffKpiRepository.save(staffKpi);
        return StaffKpiMapper.mapToStaffKpiDto(saveStaffKpi);
    }

    @Override
    public StaffKpiDto updateStaffKpi(Integer Id, StaffKpiDto staffKpiDto) {
        StaffKpi staffKpi = staffKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        Staff staff = staffRepository.findById(staffKpiDto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + staffKpiDto.getStaffId()));
        staffKpi.setStaff(staff);
        staffKpi.setDuration(staffKpiDto.getDuration());
        staffKpi.setYear(staffKpiDto.getYear());
        staffKpi.setMonth(staffKpiDto.getMonth());
        staffKpi.setWeek(staffKpiDto.getWeek());
        staffKpi.setPgTimeGoal(staffKpiDto.getPgTimeGoal());
        staffKpi.setKpi(staffKpiDto.getKpi());
        staffKpi.setOleGoal(staffKpiDto.getOleGoal());
        staffKpi.setMachineTimeGoal(staffKpiDto.getMachineTimeGoal());
        staffKpi.setManufacturingPoint(staffKpiDto.getManufacturingPoint());
        staffKpi.setUpdatedDate(updatedTimestamp);
        StaffKpi saveStaffKpi = staffKpiRepository.save(staffKpi);
        return StaffKpiMapper.mapToStaffKpiDto(saveStaffKpi);
    }

    @Override
    public StaffKpiDto getStaffKpiById(Integer Id) {
        StaffKpi staffKpi = staffKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + Id));
        return StaffKpiMapper.mapToStaffKpiDto(staffKpi);
    }

    @Override
    public void deleteStaffKpi(Integer Id) {
        StaffKpi staffKpi = staffKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Staff is not found:" + Id));
        staffKpiRepository.delete(staffKpi);
    }

    @Override
    public List<StaffKpiDto> getStaffKpis() {
        List<StaffKpi> staffKpis = staffKpiRepository.findAll();
        return staffKpis.stream().map(StaffKpiMapper::mapToStaffKpiDto).toList();
    }

}
