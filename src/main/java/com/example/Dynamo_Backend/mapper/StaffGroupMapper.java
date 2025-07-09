package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.StaffGroupDto;
import com.example.Dynamo_Backend.entities.StaffGroup;

public class StaffGroupMapper {
        public static StaffGroup mapToStaffGroup(StaffGroupDto staffGroupDto) {
                StaffGroup staffGroup = new StaffGroup();
                staffGroup.setStaffGroupId(staffGroupDto.getStaffGroupId());
                staffGroup.setGroup(staffGroup.getGroup());
                staffGroup.setStaff(staffGroup.getStaff());
                // staffGroup.setCreatedDate(staffGroupDto.getCreatedDate());
                // staffGroup.setUpdatedDate(staffGroupDto.getUpdatedDate());
                return staffGroup;
        }

        public static StaffGroupDto mapToStaffGroupDto(StaffGroup staffGroup) {
                StaffGroupDto staffGroupDto = new StaffGroupDto();
                String formattedCreatedDate = Instant.ofEpochMilli(staffGroup.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String formattedUpdatedDate = Instant.ofEpochMilli(staffGroup.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                staffGroupDto.setStaffGroupId(staffGroup.getStaffGroupId());
                staffGroupDto.setGroupId(
                                staffGroup.getGroup() != null ? staffGroup.getGroup().getGroupId() : null);
                staffGroupDto
                                .setStaffId(staffGroup.getStaff() != null ? staffGroup.getStaff().getId() : null);
                staffGroupDto.setStaffName(
                                staffGroup.getStaff() != null ? staffGroup.getStaff().getStaffName() : null);
                staffGroupDto.setCreatedDate(formattedCreatedDate);
                staffGroupDto.setUpdatedDate(formattedUpdatedDate);
                return staffGroupDto;
        }

        public static StaffGroupDto mapStaffGroupStatusDto(StaffGroup staffGroup, int status) {
                StaffGroupDto staffGroupDto = new StaffGroupDto();
                staffGroupDto.setStaffGroupId(staffGroup.getStaffGroupId());
                staffGroupDto.setGroupId(
                                staffGroup.getGroup() != null ? staffGroup.getGroup().getGroupId() : null);
                staffGroupDto
                                .setStaffId(staffGroup.getStaff() != null ? staffGroup.getStaff().getId() : null);
                staffGroupDto.setStaffName(
                                staffGroup.getStaff() != null ? staffGroup.getStaff().getStaffName() : null);
                staffGroupDto.setStatus(status);
                return staffGroupDto;
        }
}
