package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.OrderDto;

public interface OrderService {

    OrderDto addOrder(OrderDto orderDto);

    OrderDto updateOrder(String orderId, OrderDto orderDto);

    OrderDto getOrderById(String orderId);

    void deleteOrder(String orderId);

    List<OrderDto> getAllOrder();
}
