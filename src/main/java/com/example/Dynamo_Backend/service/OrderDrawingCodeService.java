package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.OrderDrawingCodeDto;

public interface OrderDrawingCodeService {
    OrderDrawingCodeDto addOrderDrawingCode(OrderDrawingCodeDto orderDrawingCodeDto);

    OrderDrawingCodeDto updateOrderDrawingCode(String Id, OrderDrawingCodeDto orderDrawingCodeDto);

    OrderDrawingCodeDto getOrderDrawingCodeById(String Id);

    void deleteOrderDrawingCode(String Id);

    List<OrderDrawingCodeDto> getOrderDrawingCodes();

}
