package com.example.Dynamo_Backend.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminRequestDto {
    private String Id;
    private String email;
    private String password;
}
