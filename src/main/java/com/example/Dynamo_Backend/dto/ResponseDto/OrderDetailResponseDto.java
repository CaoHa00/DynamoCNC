package com.example.Dynamo_Backend.dto.ResponseDto;

import com.example.Dynamo_Backend.dto.ProcessTimeSummaryDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderDetailResponseDto {
    private String orderDetailId;
    private String drawingCodeId;
    private String drawingCodeName;
    private String orderId;
    private String poNumber;
    private String orderCode;
    private Integer quantity;
    private String orderType;
    private String createdDate;
    private String updatedDate;
    private Float pgTimeGoal;
    private String managerGroupId;
    private String managerGroupName;
    private ProcessTimeSummaryDto processTimeSummaryDto;
}
