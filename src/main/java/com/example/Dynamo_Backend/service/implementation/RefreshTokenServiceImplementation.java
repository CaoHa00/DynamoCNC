package com.example.Dynamo_Backend.service.implementation;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.RequestDto.LoginRequest;
import com.example.Dynamo_Backend.dto.ResponseDto.AuthResponseDto;
import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.RefreshToken;
import com.example.Dynamo_Backend.entities.Role;
import com.example.Dynamo_Backend.repository.AdminRepository;
import com.example.Dynamo_Backend.repository.RefreshTokenRepository;
import com.example.Dynamo_Backend.security.JwtUtil;
import com.example.Dynamo_Backend.service.RefreshTokenService;

@Service
public class RefreshTokenServiceImplementation implements RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${refresh.token.expiration}")
    private long EXPIRATION;

    @Override
    public AuthResponseDto refreshToken(String requestToken) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(requestToken);
        if (refreshTokenOpt.isEmpty()) {
            throw new RuntimeException("Invalid refresh token");
        }
        RefreshToken refreshToken = refreshTokenOpt.get();
        if (refreshToken.getExpiryDate().before(new Date(System.currentTimeMillis()))) {
            throw new RuntimeException("Refresh token expired");
        }
        Admin admin = refreshToken.getAdmin();

        Set<Role> roles = admin.getRoles();
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        String newJwt = jwtUtil.generateToken(admin.getId(), admin.getEmail(), authorities);
        // Rotate refresh token for better security
        String newRefreshToken = UUID.randomUUID().toString();
        refreshToken.setToken(newRefreshToken);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + EXPIRATION));
        refreshTokenRepository.save(refreshToken);
        return new AuthResponseDto(newJwt, newRefreshToken);
    }

    @Override
    public String generateRefreshToken(LoginRequest request) {
        RefreshToken refreshToken = new RefreshToken();
        String identifier = (request.getUsername() != null && !request.getUsername().isEmpty())
                ? request.getUsername()
                : request.getEmail();
        Admin admin = adminRepository.findByUsername(identifier)
                .or(() -> adminRepository.findByEmail(identifier))
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        String newRefreshToken = UUID.randomUUID().toString();
        refreshToken.setToken(newRefreshToken);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + EXPIRATION));
        refreshToken.setAdmin(admin);
        refreshTokenRepository.save(refreshToken);
        return newRefreshToken;
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}
