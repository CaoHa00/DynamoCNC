package com.example.Dynamo_Backend.dto.ResponseDto;

import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.StaffDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrentStatusResponseDto {
    private String id;
    private MachineDto machineDto;
    private StaffDto staffDto;
    private String drawingCodeName;
    private Integer pgTime;
    private Long startTime;
    private String time;
    private String status;
}
