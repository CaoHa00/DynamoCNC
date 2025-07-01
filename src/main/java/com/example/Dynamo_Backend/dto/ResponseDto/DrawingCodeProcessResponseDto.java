package com.example.Dynamo_Backend.dto.ResponseDto;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.dto.MachineDto;

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
    private Long pgTime;
    private Long startTime;
    private Long endTime;
    private String createdDate;
    private String updatedDate;
    private Integer status;
    private DrawingCodeDto drawingCodeDto;
    private MachineDto machineDto;
}
