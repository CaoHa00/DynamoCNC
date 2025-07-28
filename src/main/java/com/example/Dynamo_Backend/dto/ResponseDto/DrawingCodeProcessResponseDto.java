package com.example.Dynamo_Backend.dto.ResponseDto;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.OrderDetailDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DrawingCodeProcessResponseDto {
    private String processId;
    private Integer partNumber;
    private Integer stepNumber;
    private Integer manufacturingPoint;
    private String processType;
    private Integer processStatus;
    // private Long totalRunningTime;
    // private Long totalStopTime;
    // private Long offsetRunTime;
    // private Long pgRunTime;
    private String startTime;
    private String endTime;
    private String createdDate;
    private String updatedDate;
    private Integer isPlan;
    private Integer status;
    private OrderDetailDto orderDetailDto;
    private MachineDto machineDto;
}
