package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.StaffDto;
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
                staff.setStaffStep(staffDto.getStaffStep());
                staff.setStaffKpis(staffDto.getStaffKpiDtos().stream().map(StaffKpiMapper::mapToStaffKpi).toList());
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
                staffDto.setStaffStep(staff.getStaffStep());
                staffDto.setStaffSection(staff.getStaffSection());
                staffDto.setStatus(staff.getStatus());
                staffDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(staff.getCreatedDate()));
                staffDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(staff.getUpdatedDate()));
                staffDto.setStaffKpiDtos(staff.getStaffKpis().stream().map(StaffKpiMapper::mapToStaffKpiDto).toList());

                return staffDto;
        }
}
