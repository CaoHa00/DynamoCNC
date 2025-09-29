package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffCurrentStatusResponse {
    private String machineName;
    private String orderCode;
    private String partNumber;
    private String stepNumber;
    private Float pgTime;
    private String time;
    private String status;
}
