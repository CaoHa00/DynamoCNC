package com.example.Dynamo_Backend.dto.RequestDto;

import lombok.Data;

@Data
public class UpdatedProcessDto {
    private String processId;
    private Long startTime;
    private Long endTime;
}
