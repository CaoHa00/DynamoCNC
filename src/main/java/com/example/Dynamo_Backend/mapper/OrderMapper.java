package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.OrderDto;
import com.example.Dynamo_Backend.entities.Order;

public class OrderMapper {
    public static Order mapToOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setOrderId(orderDto.getOrderId());
        order.setPoNumber(orderDto.getPoNumber());
        return order;
    }

    public static OrderDto mapToOrderDto(Order order) {
        return new OrderDto(
                order.getOrderId(),
                order.getPoNumber(),
                order.getDrawingCode() != null ? order.getDrawingCode().getDrawingCodeId() : null);

    }

}
