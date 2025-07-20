package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.RequestDto.AdminRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.AdminResponseDto;

public interface AdminService {
    AdminResponseDto register(AdminRequestDto adminRequestDto);

    AdminResponseDto login(AdminRequestDto adminRequestDto);

    AdminResponseDto getAdminById(String Id);

    AdminResponseDto updateAdmin(String Id, AdminRequestDto adminRequestDto);

    void deleteAdmin(String Id);

    List<AdminResponseDto> getAllAdmins();
}
