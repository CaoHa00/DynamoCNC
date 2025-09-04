package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.RequestDto.AdminRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.AdminResponseDto;
import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class AdminMapper {
    public static Admin mapToAdmin(AdminRequestDto adminDto) {
        Admin admin = new Admin();
        admin.setEmail(adminDto.getEmail());
        admin.setPassword(adminDto.getPassword());
        return admin;
    }

    public static AdminResponseDto mapToAdminResponseDto(Admin admin) {
        AdminResponseDto adminResponseDto = new AdminResponseDto();
        adminResponseDto.setEmail(admin.getEmail());
        adminResponseDto.setId(admin.getId());
        adminResponseDto.setUsername(admin.getUsername());
        adminResponseDto.setFullname(admin.getFullname());
        adminResponseDto.setRole(admin.getRoles());
        adminResponseDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(admin.getCreatedDate()));
        adminResponseDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(admin.getUpdatedDate()));

        return adminResponseDto;
    }
}
