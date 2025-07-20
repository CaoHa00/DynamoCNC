package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.Dynamo_Backend.dto.RequestDto.AdminRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.AdminResponseDto;
import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.mapper.AdminMapper;
import com.example.Dynamo_Backend.repository.AdminRepository;
import com.example.Dynamo_Backend.service.AdminService;

public class AdminImplement implements AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    // private PasswordEncoder passwordEncoder;
    @Override
    public AdminResponseDto register(AdminRequestDto adminRequestDto) {
        if (adminRepository.existsByEmail(adminRequestDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Admin admin = new Admin();
        long createdTimestamp = System.currentTimeMillis();
        admin.setCreatedDate(createdTimestamp);
        admin.setUpdatedDate(createdTimestamp);
        admin.setEmail(adminRequestDto.getEmail());
        // admin.setPassword(passwordEncoder.encode(adminRequestDto.getPassword()));
        admin.setPassword(adminRequestDto.getPassword());
        Admin savedAdmin = adminRepository.save(admin);
        return AdminMapper.mapToaAdminResponseDto(savedAdmin);
    }

    @Override
    public AdminResponseDto login(AdminRequestDto adminRequestDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

    @Override
    public AdminResponseDto getAdminById(String Id) {
        Admin admin = adminRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return AdminMapper.mapToaAdminResponseDto(admin);
    }

    @Override
    public AdminResponseDto updateAdmin(String Id, AdminRequestDto adminRequestDto) {
        Admin admin = adminRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        long updatedTimestamp = System.currentTimeMillis();
        admin.setEmail(adminRequestDto.getEmail());
        admin.setUpdatedDate(updatedTimestamp);

        Admin savedAdmin = adminRepository.save(admin);

        return AdminMapper.mapToaAdminResponseDto(savedAdmin);
    }

    @Override
    public void deleteAdmin(String Id) {
        Admin admin = adminRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        adminRepository.delete(admin);
    }

    @Override
    public List<AdminResponseDto> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream().map(AdminMapper::mapToaAdminResponseDto).toList();
    }

}
