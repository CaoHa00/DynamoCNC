package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDrawingCodeDto {
    private String orderDrawingCodeId;
    private String drawingCodeId;
    private String orderId;
    private String createdDate;
    private String updatedDate;
    private int status;

}
