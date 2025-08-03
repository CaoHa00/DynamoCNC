package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.OrderDetailDto;

public interface OrderDetailService {
    OrderDetailDto addOrderDetail(OrderDetailDto orderDetailDto);

    OrderDetailDto updateOrderDetail(String Id, OrderDetailDto orderDetailDto);

    OrderDetailDto getOrderDetailById(String Id);

    void deleteOrderDetail(String Id);

    List<OrderDetailDto> getOrderDetails();

}
