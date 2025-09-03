package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.Set;

import com.example.Dynamo_Backend.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String userId;
    private String email;
    private String username;
    private String fullname;
    private Set<Role> role;
}