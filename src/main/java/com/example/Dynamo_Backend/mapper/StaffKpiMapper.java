package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.StaffKpiDto;
import com.example.Dynamo_Backend.dto.RequestDto.StaffRequestDto;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class StaffKpiMapper {
    StaffRepository staffRepository;

    public static StaffKpi mapToStaffKpi(StaffKpiDto staffKpiDto) {
        StaffKpi staffKpi = new StaffKpi();
        // Staff staff = staffRepository.findById(staffKpiDto.getStaffId())
        // .orElseThrow(() -> new RuntimeException("Staff is not found:" +
        // staffKpiDto.getStaffId()));
        // staffKpi.setStaff(staff);
        staffKpi.setYear(staffKpiDto.getYear());
        staffKpi.setMonth(staffKpiDto.getMonth());
        staffKpi.setPgTimeGoal(staffKpiDto.getPgTimeGoal());
        staffKpi.setKpi(staffKpiDto.getKpi());
        staffKpi.setOleGoal(staffKpiDto.getOleGoal());
        staffKpi.setWorkGoal(staffKpiDto.getWorkGoal());
        staffKpi.setMachineTimeGoal(staffKpiDto.getMachineTimeGoal());
        staffKpi.setManufacturingPoint(staffKpiDto.getManufacturingPoint());
        // staffKpi.setCreatedDate((long) 0);
        // staffKpi.setUpdatedDate((long) 0);
        return staffKpi;
    }

    public static StaffKpiDto mapToStaffKpiDto(StaffRequestDto staffRequestDto) {
        StaffKpiDto staffKpiDto = new StaffKpiDto();
        staffKpiDto.setStaffId(staffRequestDto.getId());
        staffKpiDto.setYear(staffRequestDto.getYear());
        staffKpiDto.setMonth(staffRequestDto.getMonth());
        staffKpiDto.setPgTimeGoal(staffRequestDto.getPgTimeGoal());
        staffKpiDto.setKpi(staffRequestDto.getKpi());
        staffKpiDto.setOleGoal(staffRequestDto.getOleGoal());
        staffKpiDto.setWorkGoal(staffRequestDto.getWorkGoal());
        staffKpiDto.setWorkGoal(staffRequestDto.getWorkGoal());
        staffKpiDto.setMachineTimeGoal(staffRequestDto.getMachineTimeGoal());
        staffKpiDto.setManufacturingPoint(staffRequestDto.getManufacturingPoint());
        staffKpiDto.setGroupId(staffRequestDto.getGroupId());
        return staffKpiDto;
    }

    public static StaffKpiDto mapToStaffKpiDto(StaffKpi staffKpi) {
        StaffKpiDto staffKpiDto = new StaffKpiDto();
        staffKpiDto.setKpiId(staffKpi.getId());
        staffKpiDto.setId(staffKpi.getStaff().getStaffId());
        staffKpiDto.setStaffName(staffKpi.getStaff().getStaffName());
        staffKpiDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(staffKpi.getCreatedDate()));
        staffKpiDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(staffKpi.getUpdatedDate()));
        staffKpiDto.setStaffId(staffKpi.getStaff().getId());
        staffKpiDto.setGroupId(staffKpi.getGroup().getGroupId());
        staffKpiDto.setGroupName(staffKpi.getGroup().getGroupName());
        staffKpiDto.setYear(staffKpi.getYear());
        staffKpiDto.setMonth(staffKpi.getMonth());
        staffKpiDto.setPgTimeGoal(staffKpi.getPgTimeGoal());
        staffKpiDto.setKpi(staffKpi.getKpi());
        staffKpiDto.setOleGoal(staffKpi.getOleGoal());
        staffKpiDto.setWorkGoal(staffKpi.getWorkGoal());
        staffKpiDto.setMachineTimeGoal(staffKpi.getMachineTimeGoal());
        staffKpiDto.setManufacturingPoint(staffKpi.getManufacturingPoint());
        staffKpiDto.setStaffStatus(staffKpi.getStaff().getStatus());
        return staffKpiDto;
    }

    public static StaffKpiDto mapToStaffDto(StaffKpi staffKpi) {
        StaffKpiDto staffKpiDto = new StaffKpiDto();
        staffKpiDto.setStaffId(staffKpi.getStaff().getId());
        ;
        staffKpiDto.setStaffName(staffKpi.getStaff().getStaffName());
        return staffKpiDto;
    }

}
