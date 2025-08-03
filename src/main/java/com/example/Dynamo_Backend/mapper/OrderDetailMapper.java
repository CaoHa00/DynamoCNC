package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class OrderDetailMapper {
    public static OrderDetailDto mapToOrderDetailDto(OrderDetail OrderDetail) {
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        orderDetailDto.setOrderDetailId(OrderDetail.getOrderDetailId());
        orderDetailDto.setDrawingCodeId(OrderDetail.getDrawingCode().getDrawingCodeId());
        orderDetailDto.setOrderId(OrderDetail.getOrder().getOrderId());
        orderDetailDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(OrderDetail.getCreatedDate()));
        orderDetailDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(OrderDetail.getUpdatedDate()));
        orderDetailDto.setOrderCode(OrderDetail.getOrderCode());
        orderDetailDto.setOrderType(OrderDetail.getOrderType());
        orderDetailDto.setQuantity(OrderDetail.getQuantity());
        return orderDetailDto;
    }

    public static OrderDetail mapToOrderDetail(OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailId(orderDetailDto.getOrderDetailId());
        orderDetail.setOrderCode(orderDetailDto.getOrderCode());
        orderDetail.setOrderType(orderDetailDto.getOrderType());
        orderDetail.setQuantity(orderDetailDto.getQuantity());
        // orderDetail.setStatus(orderDetailDto.getStatus());
        return orderDetail;
    }
}
