package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Dynamo_Backend.dto.RequestDto.AdminRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.AdminResponseDto;
import com.example.Dynamo_Backend.service.AdminService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    public final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<AdminResponseDto>> getAlladmins() {
        List<AdminResponseDto> adminResponseDtos = adminService.getAllAdmins();
        return ResponseEntity.status(HttpStatus.OK).body(adminResponseDtos);
    }

    @PostMapping("/register")
    public ResponseEntity<AdminResponseDto> register(@RequestBody AdminRequestDto adminRequestDto) {
        AdminResponseDto adminResponseDto = adminService.register(adminRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminResponseDto);
    }

    @PutMapping("/{admin_id}")
    public ResponseEntity<AdminResponseDto> updateAdmin(@PathVariable("admin_id") String Id,
            @RequestBody AdminRequestDto adminRequestDto) {
        AdminResponseDto adminResponseDto = adminService.updateAdmin(Id, adminRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminResponseDto);
    }

    @DeleteMapping("/{admin_id}")
    public ResponseEntity<Void> deleteadmin(@PathVariable("admin_id") String Id) {
        adminService.deleteAdmin(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{admin_id}")
    public ResponseEntity<AdminResponseDto> getAdminById(@PathVariable("admin_id") String Id) {
        AdminResponseDto adminResponseDto = adminService.getAdminById(Id);
        return ResponseEntity.status(HttpStatus.OK).body(adminResponseDto);
    }
}
