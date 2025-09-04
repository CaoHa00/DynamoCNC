package com.example.Dynamo_Backend.service;

import com.example.Dynamo_Backend.dto.RequestDto.LoginRequest;
import com.example.Dynamo_Backend.dto.ResponseDto.AuthResponseDto;

public interface RefreshTokenService {
    void logout(String refreshToken);

    AuthResponseDto refreshToken(String request);

    String generateRefreshToken(LoginRequest request);
}