package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OperateHistoryDto {
    private String operateHistoryId;
    private Integer manufacturingPoint;
    private Long startTime;
    private Long stopTime;
    private Integer inProgress;
    private String staffId;
    private String drawingCodeProcessId;
}
