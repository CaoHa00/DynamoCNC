package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CurrentStaffDto {
    private Long Id;
    private String staffId;
    private Integer machineId;
    private String assignedAt;
}
