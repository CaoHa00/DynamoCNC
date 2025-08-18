package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderDetailResponseDto;
import com.example.Dynamo_Backend.entities.DrawingCode;
import com.example.Dynamo_Backend.entities.Order;

public interface OrderDetailService {
    OrderDetailDto addOrderDetail(OrderDetailDto orderDetailDto);

    OrderDetailDto updateOrderDetail(String Id, OrderDetailDto orderDetailDto);

    OrderDetailDto getOrderDetailById(String Id);

    void deleteOrderDetail(String Id);

    List<OrderDetailResponseDto> getOrderDetails();

    void updateOrderCode(String drawingCodeId, String orderId);
}
