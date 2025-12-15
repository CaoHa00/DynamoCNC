package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.Set;

import com.example.Dynamo_Backend.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminResponseDto {
    private String Id;
    private String email;
    private String username;
    private String fullname;
    private Set<Role> role;
    private String createdDate;
    private String updatedDate;
}
