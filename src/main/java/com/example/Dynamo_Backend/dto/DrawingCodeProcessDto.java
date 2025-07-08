package com.example.Dynamo_Backend.dto;

import java.util.List;

import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.Log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DrawingCodeProcessDto {

    private String processId;
    private Integer partNumber;
    private Integer stepNumber;
    private Integer manufacturingPoint;
    private Long totalRunningTime;
    private Long totalStopTime;
    private Long offsetRunTime;
    private Long pgRunTime;
    private Long startTime;
    private Long endTime;
    private String createdDate;
    private String updatedDate;
    private Integer status;
    private String qcNote;
    private String orderDetailId;
    private Integer machineId;
    private List<OperateHistory> staffHistories;
    private List<Log> statstistics;
}
