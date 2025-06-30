package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.OrderDto;
import com.example.Dynamo_Backend.entities.Order;

public class OrderMapper {
    public static Order mapToOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setOrderId(orderDto.getOrderId());
        order.setPoNumber(orderDto.getPoNumber());
        // order.setCreatedDate(orderDto.getCreatedDate());
        // order.setUpdatedDate(orderDto.getUpdatedDate());
        return order;
    }

    public static OrderDto mapToOrderDto(Order order) {
        String formattedCreatedDate = Instant.ofEpochMilli(order.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(order.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return new OrderDto(
                order.getOrderId(),
                order.getPoNumber(),
                order.getDrawingCode() != null ? order.getDrawingCode().getDrawingCodeId() : null,
                formattedCreatedDate,
                formattedUpdatedDate);

    }

}
