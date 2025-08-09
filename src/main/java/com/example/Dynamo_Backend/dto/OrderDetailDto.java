package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
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
    // private int status;

    public OrderDetailDto(String orderDetailId, String orderCode) {
        this.orderDetailId = orderDetailId;
        this.orderCode = orderCode;
    }

}
