package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.OrderDrawingCodeDto;
import com.example.Dynamo_Backend.entities.OrderDrawingCode;

public class OrderDrawingCodeMapper {
    public static OrderDrawingCodeDto mapToOrderDrawingCodeDto(OrderDrawingCode OrderDrawingCode) {
        OrderDrawingCodeDto orderDrawingCodeDto = new OrderDrawingCodeDto();

        String formattedCreatedDate = Instant.ofEpochMilli(OrderDrawingCode.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(OrderDrawingCode.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        orderDrawingCodeDto.setOrderDrawingCodeId(OrderDrawingCode.getOrderDrawingCodeId());
        orderDrawingCodeDto.setDrawingCodeId(OrderDrawingCode.getDrawingCode().getDrawingCodeId());
        orderDrawingCodeDto.setOrderId(OrderDrawingCode.getOrder().getOrderId());
        orderDrawingCodeDto.setCreatedDate(formattedCreatedDate);
        orderDrawingCodeDto.setUpdatedDate(formattedUpdatedDate);
        return orderDrawingCodeDto;
    }

    public static OrderDrawingCode mapToOrderDrawingCode(OrderDrawingCodeDto orderDrawingCodeDto) {
        OrderDrawingCode orderDrawingCode = new OrderDrawingCode();
        orderDrawingCode.setOrderDrawingCodeId(orderDrawingCodeDto.getOrderDrawingCodeId());
        return orderDrawingCode;
    }
}
