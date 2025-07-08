package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.entities.OrderDetail;

public class OrderDetailMapper {
    public static OrderDetailDto mapToOrderDetailDto(OrderDetail OrderDetail) {
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        String formattedCreatedDate = Instant.ofEpochMilli(OrderDetail.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(OrderDetail.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        orderDetailDto.setOrderDetailId(OrderDetail.getOrderDetailId());
        orderDetailDto.setDrawingCodeId(OrderDetail.getDrawingCode().getDrawingCodeId());
        orderDetailDto.setOrderId(OrderDetail.getOrder().getOrderId());
        orderDetailDto.setCreatedDate(formattedCreatedDate);
        orderDetailDto.setUpdatedDate(formattedUpdatedDate);
        orderDetailDto.setOrderCode(OrderDetail.getOrderCode());
        orderDetailDto.setPgTime(OrderDetail.getPgTime());
        orderDetailDto.setProductionStep(OrderDetail.getProductionStep());
        orderDetailDto.setQuantity(OrderDetail.getQuantity());
        return orderDetailDto;
    }

    public static OrderDetail mapToOrderDetail(OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailId(orderDetailDto.getOrderDetailId());
        return orderDetail;
    }
}
