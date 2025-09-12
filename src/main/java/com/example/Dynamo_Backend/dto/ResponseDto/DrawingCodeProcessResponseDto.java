package com.example.Dynamo_Backend.dto.ResponseDto;

import java.util.List;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.dto.PlanDto;
import com.example.Dynamo_Backend.dto.ProcessTimeDto;
import com.example.Dynamo_Backend.dto.StaffDto;

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
    private Float pgTime;
    private String startTime;
    private String endTime;
    private String createdDate;
    private String updatedDate;
    private Integer isPlan;
    private Integer status;
    private OrderDetailDto orderDetailDto;
    private MachineDto machineDto;
    private List<StaffDto> staffDtos;
    private PlanDto planDto;
    private ProcessTimeDto processTimeDto;
}
