package com.example.Dynamo_Backend.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class StaffMapper {
        public static Staff mapToStaff(StaffDto staffDto) {
                Staff staff = new Staff();
                staff.setId(staffDto.getId());
                staff.setStaffId(staffDto.getStaffId());
                staff.setStaffName(staffDto.getStaffName());
                staff.setStaffOffice(staffDto.getStaffOffice());
                staff.setStaffSection(staffDto.getStaffSection());
                staff.setShortName(staffDto.getShortName());
                // staff.setStatus(staffDto.getStatus());
                // staff.setDateAdd(staffDto.getDateAdd());
                return staff;

        }

        public static StaffDto mapToStaffDto(Staff staff) {
                StaffDto staffDto = new StaffDto();
                staffDto.setId(staff.getId());
                staffDto.setStaffId(staff.getStaffId());
                staffDto.setStaffName(staff.getStaffName());
                staffDto.setStaffOffice(staff.getStaffOffice());
                staffDto.setShortName(staff.getShortName());
                staffDto.setStaffSection(staff.getStaffSection());
                staffDto.setStatus(staff.getStatus());
                staffDto.setGroupId(staff.getGroup().getGroupId());
                staffDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(staff.getCreatedDate()));
                staffDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(staff.getUpdatedDate()));
                String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
                StaffKpi staffKpi = null;
                for (StaffKpi mk : staff.getStaffKpis()) {
                        if (currentMonth.equals(String.format("%02d", mk.getMonth()))) {
                                staffKpi = mk;
                        }
                }
                if (staffKpi != null) {
                        staffDto.setStaffKpiDtos(StaffKpiMapper.mapToStaffKpiDto(staffKpi));
                }
                return staffDto;
        }
}
