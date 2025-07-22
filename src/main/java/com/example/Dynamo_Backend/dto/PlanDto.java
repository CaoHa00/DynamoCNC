package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlanDto {
    private Integer Id;
    private String startTime;
    private String endTime;
    private Integer status;
    private Integer inProgress;
    private Float remark;
    private Long remarkTime;
    private String processId;
    private String staffId;
    private Integer machineId;
    private String plannerId;
    private String createdDate;
    private String updatedDate;
}
