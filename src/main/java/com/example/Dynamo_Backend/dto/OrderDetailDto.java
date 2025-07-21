package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
    private String orderDetailId;
    private String drawingCodeId;
    private String orderId;
    private String orderCode;
    private int quantity;
    private String orderType;
    private String createdDate;
    private String updatedDate;
    private int status;

}
