package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.dto.ProcessTimeSummaryDto;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderDetailResponseDto;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class OrderDetailMapper {
    public static OrderDetailDto mapToOrderDetailDto(OrderDetail orderDetail) {
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        orderDetailDto.setOrderDetailId(orderDetail.getOrderDetailId());
        orderDetailDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(orderDetail.getCreatedDate()));
        orderDetailDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(orderDetail.getUpdatedDate()));
        orderDetailDto.setOrderCode(orderDetail.getOrderCode());
        orderDetailDto.setOrderType(orderDetail.getOrderType());
        orderDetailDto.setQuantity(orderDetail.getQuantity());
        orderDetailDto.setNumberOfSteps(orderDetail.getNumberOfStep());
        orderDetailDto.setOffice(orderDetailDto.getOffice());
        orderDetailDto.setManagerGroupId(
                orderDetail.getManagerGroup() != null ? orderDetail.getManagerGroup().getGroupId() : null);
        orderDetailDto.setPgTimeGoal(orderDetail.getPgTimeGoal());
        orderDetailDto.setProgress(orderDetail.getProgress());
        return orderDetailDto;
    }

    public static OrderDetailResponseDto mapToOrderDetailResponseDto(OrderDetail orderDetail,
            ProcessTimeSummaryDto processTimeSummaryDto) {
        OrderDetailResponseDto orderDetailDto = new OrderDetailResponseDto();

        orderDetailDto.setOrderDetailId(orderDetail.getOrderDetailId());
        System.out.println(orderDetail.getOrderCode());
        System.out.println(orderDetail.getOffice());
        orderDetailDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(orderDetail.getCreatedDate()));
        orderDetailDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(orderDetail.getUpdatedDate()));
        orderDetailDto.setOrderCode(orderDetail.getOrderCode());
        orderDetailDto.setOrderType(orderDetail.getOrderType());
        orderDetailDto.setQuantity(orderDetail.getQuantity());
        orderDetailDto.setNumberOfSteps(orderDetail.getNumberOfStep());
        orderDetailDto.setOffice(orderDetail.getOffice());
        orderDetailDto.setManagerGroupId(
                orderDetail.getManagerGroup() != null ? orderDetail.getManagerGroup().getGroupId() : null);
        orderDetailDto.setManagerGroupName(
                orderDetail.getManagerGroup() != null ? orderDetail.getManagerGroup().getGroupName() : null);
        orderDetailDto.setPgTimeGoal(orderDetail.getPgTimeGoal());
        if (processTimeSummaryDto != null) {
            orderDetailDto.setProcessTimeSummaryDto(processTimeSummaryDto);
        }
        orderDetailDto.setProgress(orderDetail.getProgress());
        return orderDetailDto;
    }

    public static OrderDetail mapToOrderDetail(OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailId(orderDetailDto.getOrderDetailId());
        orderDetail.setOrderCode(orderDetailDto.getOrderCode());
        orderDetail.setOrderType(orderDetailDto.getOrderType());
        orderDetail.setQuantity(orderDetailDto.getQuantity());
        orderDetail.setPgTimeGoal(orderDetailDto.getPgTimeGoal());
        orderDetail.setNumberOfStep(orderDetailDto.getNumberOfSteps());
        orderDetail.setOffice(orderDetailDto.getOffice());
        // orderDetail.setStatus(orderDetailDto.getStatus());
        orderDetail.setProgress(orderDetailDto.getProgress());
        return orderDetail;
    }

    public static OrderDetailDto mapOrderCodeDto(OrderDetail orderDetail) {
        if (orderDetail == null)
            return null;
        return new OrderDetailDto(orderDetail.getOrderDetailId(), orderDetail.getOrderCode());
    }
}
