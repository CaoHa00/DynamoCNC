package com.example.Dynamo_Backend.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DrawingCodeProcessResquestDto {
    private String processId;
    private String processType;
    private Integer partNumber;
    private Integer stepNumber;
    private Integer manufacturingPoint;
    private Float pgTime;
    private String startTime;
    private String endTime;
    private Integer status;
    private String orderCode;
    private Integer machineId;
    private Integer staffId;
    private String plannerId;
    private Integer inProgress;
    private Float remark;
    private String remarkTime;
    private Integer isPlan;
}
