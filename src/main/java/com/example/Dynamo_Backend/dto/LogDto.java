package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogDto {
    private String logId;
    private Long timeStamp;
    private String status;
    // private String drawingCodeProcessId;
    private Integer machineId;
    private String staffId;

}
