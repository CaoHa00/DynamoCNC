package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.RequestDto.AdminRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.AdminResponseDto;
import com.example.Dynamo_Backend.entities.Admin;

public class AdminMapper {
    public static Admin mapToAdmin(AdminRequestDto adminDto) {
        Admin admin = new Admin();
        admin.setEmail(adminDto.getEmail());
        admin.setPassword(adminDto.getPassword());
        return admin;
    }

    public static AdminResponseDto mapToaAdminResponseDto(Admin admin) {
        String formattedCreatedDate = Instant.ofEpochMilli(admin.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(admin.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        AdminResponseDto adminResponseDto = new AdminResponseDto();
        adminResponseDto.setEmail(admin.getEmail());
        adminResponseDto.setId(admin.getId());
        adminResponseDto.setCreatedDate(formattedCreatedDate);
        adminResponseDto.setUpdatedDate(formattedUpdatedDate);

        return adminResponseDto;
    }
}
