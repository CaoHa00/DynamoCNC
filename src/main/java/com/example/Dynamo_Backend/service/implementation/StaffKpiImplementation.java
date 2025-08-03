package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        Staff staff = staffRepository.findByStaffId(staffKpiDto.getStaffId())
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
        Staff staff = staffRepository.findByStaffId(staffKpiDto.getStaffId())
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
    public StaffKpiDto updateStaffKpiByStaffId(Integer staffId, StaffKpiDto staffKpiDto) {
        long updatedTimestamp = System.currentTimeMillis();
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        StaffKpi staffKpi = staffKpiRepository.findByStaff_staffId(staffId).stream()
                .filter(kpi -> currentMonth.equals(String.format("%02d", kpi.getMonth())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No staffKpi found for staff ID: " + staffId));

        Staff staff = staffRepository.findByStaffId(staffId)
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

}
