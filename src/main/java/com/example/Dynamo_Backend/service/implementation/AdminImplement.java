package com.example.Dynamo_Backend.service.implementation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.RequestDto.AdminRequestDto;
import com.example.Dynamo_Backend.dto.RequestDto.RegisterRequest;
import com.example.Dynamo_Backend.dto.ResponseDto.AdminResponseDto;
import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.Role;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.AdminMapper;
import com.example.Dynamo_Backend.repository.AdminRepository;
import com.example.Dynamo_Backend.repository.RoleRepository;
import com.example.Dynamo_Backend.service.AdminService;

@Service
public class AdminImplement implements AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminResponseDto register(RegisterRequest requestDto) {
        if (adminRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        // Check username uniqueness
        if (adminRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new BusinessException("Username already exists");
        }

        Admin admin = new Admin();
        long createdTimestamp = System.currentTimeMillis();
        admin.setCreatedDate(createdTimestamp);
        admin.setUpdatedDate(createdTimestamp);
        admin.setUsername(requestDto.getUsername());
        admin.setEmail(requestDto.getEmail());
        admin.setFullname(requestDto.getFullname());
        admin.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (requestDto.getRoles() == null || requestDto.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(
                            () -> new ResourceNotFoundException(
                                    "Default role USER not found, please check the database."));
            roles.add(defaultRole);
        } else {
            for (String roleName : requestDto.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        admin.setRoles(roles);

        Admin savedAdmin = adminRepository.save(admin);
        return AdminMapper.mapToAdminResponseDto(savedAdmin);
    }

    @Override
    public AdminResponseDto getAdminById(String Id) {
        Admin admin = adminRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return AdminMapper.mapToAdminResponseDto(admin);
    }

    @Override
    public AdminResponseDto updateAdmin(String Id, AdminRequestDto adminRequestDto) {
        Admin admin = adminRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        long updatedTimestamp = System.currentTimeMillis();
        admin.setEmail(adminRequestDto.getEmail());
        admin.setUpdatedDate(updatedTimestamp);

        Admin savedAdmin = adminRepository.save(admin);

        return AdminMapper.mapToAdminResponseDto(savedAdmin);
    }

    @Override
    public void deleteAdmin(String Id) {
        Admin admin = adminRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        adminRepository.delete(admin);
    }

    @Override
    public List<AdminResponseDto> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream().map(AdminMapper::mapToAdminResponseDto).toList();
    }

}
