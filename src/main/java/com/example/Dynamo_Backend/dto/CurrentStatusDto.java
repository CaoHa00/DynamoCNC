package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CurrentStatusDto {
    private String id;
    private Integer machineId;
    private String machineName;
    private String status;
    private String time;
    private String processId;
}
