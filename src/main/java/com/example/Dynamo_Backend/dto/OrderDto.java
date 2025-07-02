package com.example.Dynamo_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDto {
    private String orderId;
    private String poNumber;
    private String createdDate;
    private String updatedDate;
    private String orderName;
    private String customerName;
    private String phoneNumber;
    private String address;
    private String orderDate;
    private String deliveryDate;
    private String completionDate;
    private String shippingMethod;
    private String remark;
    private int status;
    private int orderstatus;
}
