package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.entities.Staff;

public class StaffMapper {
        public static Staff mapToStaff(StaffDto staffDto) {
                Staff staff = new Staff();
                staff.setId(staffDto.getId());
                staff.setStaffId(staffDto.getStaffId());
                staff.setStaffName(staffDto.getStaffName());
                staff.setStaffOffice(staffDto.getStaffOffice());
                staff.setStaffSection(staffDto.getStaffSection());
                staff.setStaffStep(staffDto.getStaffStep());
                staff.setKpi(staffDto.getKpi());
                // staff.setStatus(staffDto.getStatus());
                // staff.setDateAdd(staffDto.getDateAdd());
                return staff;

        }

        public static StaffDto mapToStaffDto(Staff staff) {
                StaffDto staffDto = new StaffDto();
                String formattedCreatedDate = Instant.ofEpochMilli(staff.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String formattedUpdatedDate = Instant.ofEpochMilli(staff.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                staffDto.setId(staff.getId());
                staffDto.setStaffId(staff.getStaffId());
                staffDto.setStaffName(staff.getStaffName());
                staffDto.setStaffOffice(staff.getStaffOffice());
                staffDto.setStaffStep(staff.getStaffStep());
                staffDto.setStaffSection(staff.getStaffSection());
                staffDto.setKpi(staff.getKpi());
                staffDto.setStatus(staff.getStatus());
                staffDto.setCreatedDate(formattedCreatedDate);
                staffDto.setUpdatedDate(formattedUpdatedDate);

                return staffDto;
        }
}
