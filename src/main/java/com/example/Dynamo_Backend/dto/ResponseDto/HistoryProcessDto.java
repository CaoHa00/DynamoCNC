package com.example.Dynamo_Backend.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HistoryProcessDto {
    private String orderCode;
    private Integer partNumber;
    private Integer stepNumber;
    private String startTime;
    private String endTime;
    private String machineName;
    private Integer staffIdNumber;
    private String staffName;
    private String status;
}
