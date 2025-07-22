package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.OrderDto;
import com.example.Dynamo_Backend.entities.Order;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class OrderMapper {
    public static Order mapToOrder(OrderDto orderDto) {
        Order order = new Order();

        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
        // HH:mm:ss");
        // LocalDateTime localDateTime = LocalDateTime.parse(orderDto.getOrderDate(),
        // formatter);
        // long orderDate =
        // localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        // localDateTime = LocalDateTime.parse(orderDto.getDeliveryDate(), formatter);
        // long deliveryDate =
        // localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        // localDateTime = LocalDateTime.parse(orderDto.getCompletionDate(), formatter);
        // long completeDate =
        // localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        order.setOrderId(orderDto.getOrderId());
        order.setPoNumber(orderDto.getPoNumber());
        // order.setAddress(orderDto.getAddress());
        // order.setCompletionDate(completeDate);
        // order.setCustomerName(orderDto.getCustomerName());
        // order.setDeliveryDate(deliveryDate);
        // order.setOrderDate(orderDate);
        // order.setOrderName(orderDto.getOrderName());
        order.setOrderstatus(orderDto.getOrderstatus());
        order.setStatus(orderDto.getStatus());
        // order.setShippingMethod(orderDto.getShippingMethod());
        order.setRemark(orderDto.getRemark());
        // order.setCreatedDate(orderDto.getCreatedDate());
        // order.setUpdatedDate(orderDto.getUpdatedDate());
        return order;
    }

    public static OrderDto mapToOrderDto(Order order) {
        // String orderDate = Instant.ofEpochMilli(order.getOrderDate())
        // .atZone(ZoneId.systemDefault())
        // .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // String deliveryDate = "";
        // String completionDate = "";
        // if (order.getDeliveryDate() != null) {
        // deliveryDate = Instant.ofEpochMilli(order.getDeliveryDate())
        // .atZone(ZoneId.systemDefault())
        // .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // } else {
        // deliveryDate = "Chưa giao hàng";
        // }
        // if (order.getDeliveryDate() != null) {
        // completionDate = Instant.ofEpochMilli(order.getCompletionDate())
        // .atZone(ZoneId.systemDefault())
        // .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // } else {
        // completionDate = "Chưa hoàn thành";
        // }
        return new OrderDto(
                order.getOrderId(),
                order.getPoNumber(),
                DateTimeUtil.convertTimestampToStringDate(order.getCreatedDate()),
                DateTimeUtil.convertTimestampToStringDate(order.getUpdatedDate()),
                // order.getCustomerName(),
                // order.getPhoneNumber(),
                // order.getAddress(),
                // orderDate,
                // deliveryDate,
                // completionDate,
                // order.getShippingMethod(),
                order.getRemark(),
                order.getStatus(),
                order.getOrderstatus());

    }

}
