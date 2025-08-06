package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class OrderDetailMapper {
    public static OrderDetailDto mapToOrderDetailDto(OrderDetail orderDetail) {
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        orderDetailDto.setOrderDetailId(orderDetail.getOrderDetailId());
        orderDetailDto.setDrawingCodeId(orderDetail.getDrawingCode().getDrawingCodeId());
        orderDetailDto.setDrawingCodeName(orderDetail.getDrawingCode().getDrawingCodeName());
        orderDetailDto.setPoNumber(orderDetail.getOrder().getPoNumber());
        orderDetailDto.setOrderId(orderDetail.getOrder().getOrderId());
        orderDetailDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(orderDetail.getCreatedDate()));
        orderDetailDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(orderDetail.getUpdatedDate()));
        orderDetailDto.setOrderCode(orderDetail.getOrderCode());
        orderDetailDto.setOrderType(orderDetail.getOrderType());
        orderDetailDto.setQuantity(orderDetail.getQuantity());
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
