package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.StaffKpiDto;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.repository.StaffRepository;

public class StaffKpiMapper {
    StaffRepository staffRepository;

    public static StaffKpi mapToStaffKpi(StaffKpiDto staffKpiDto) {
        StaffKpi staffKpi = new StaffKpi();
        // Staff staff = staffRepository.findById(staffKpiDto.getStaffId())
        // .orElseThrow(() -> new RuntimeException("Staff is not found:" +
        // staffKpiDto.getStaffId()));
        // staffKpi.setStaff(staff);
        staffKpi.setDuration(staffKpiDto.getDuration());
        staffKpi.setYear(staffKpiDto.getYear());
        staffKpi.setMonth(staffKpiDto.getMonth());
        staffKpi.setWeek(staffKpiDto.getWeek());
        staffKpi.setPgTimeGoal(staffKpiDto.getPgTimeGoal());
        staffKpi.setKpi(staffKpiDto.getKpi());
        staffKpi.setOleGoal(staffKpiDto.getOleGoal());
        staffKpi.setMachineTimeGoal(staffKpiDto.getMachineTimeGoal());
        staffKpi.setManufacturingPoint(staffKpiDto.getManufacturingPoint());
        // staffKpi.setCreatedDate((long) 0);
        // staffKpi.setUpdatedDate((long) 0);
        return staffKpi;
    }

    public static StaffKpiDto mapToStaffKpiDto(StaffKpi staffKpi) {
        StaffKpiDto staffKpiDto = new StaffKpiDto();
        String formattedCreatedDate = Instant.ofEpochMilli(staffKpi.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(staffKpi.getUpdatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        staffKpiDto.setCreatedDate(formattedCreatedDate);
        staffKpiDto.setUpdatedDate(formattedUpdatedDate);
        staffKpiDto.setStaffId(staffKpi.getStaff().getId());
        staffKpiDto.setDuration(staffKpi.getDuration());
        staffKpiDto.setYear(staffKpi.getYear());
        staffKpiDto.setMonth(staffKpi.getMonth());
        staffKpiDto.setWeek(staffKpi.getWeek());
        staffKpiDto.setPgTimeGoal(staffKpi.getPgTimeGoal());
        staffKpiDto.setKpi(staffKpi.getKpi());
        staffKpiDto.setOleGoal(staffKpi.getOleGoal());
        staffKpiDto.setMachineTimeGoal(staffKpi.getMachineTimeGoal());
        staffKpiDto.setManufacturingPoint(staffKpi.getManufacturingPoint());

        return staffKpiDto;
    }
}
