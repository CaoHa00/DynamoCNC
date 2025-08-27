package com.example.Dynamo_Backend.controller;

import com.example.Dynamo_Backend.security.CustomUserDetails;
import com.example.Dynamo_Backend.dto.RequestDto.LoginRequest;
import com.example.Dynamo_Backend.dto.ResponseDto.AuthResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.LoginResponse;
import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.Role;
import com.example.Dynamo_Backend.security.JwtUtil;
import com.example.Dynamo_Backend.service.RefreshTokenService;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    // register in adminController**
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            // Get authenticated CustomUserDetails principal
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Admin admin = userDetails.getAdmin();
            String token = jwtUtil.generateToken(admin.getEmail(),
                    admin.getRoles().stream()
                            .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                    role.getName()))
                            .collect(java.util.stream.Collectors.toList()));
            String userId = admin.getId();
            String email = admin.getEmail();
            Set<Role> roles = admin.getRoles();
            String refreshToken = refreshTokenService.generateRefreshToken(request);
            return new LoginResponse(token, refreshToken, userId, email, roles);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String refreshToken) {
        refreshTokenService.logout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody String request) {
        AuthResponseDto response = refreshTokenService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

}