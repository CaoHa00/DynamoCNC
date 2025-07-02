package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.dto.OrderDto;
import com.example.Dynamo_Backend.dto.OrderDrawingCodeDto;
import com.example.Dynamo_Backend.entities.DrawingCode;
import com.example.Dynamo_Backend.entities.OrderDrawingCode;
import com.example.Dynamo_Backend.entities.Order;
import com.example.Dynamo_Backend.mapper.DrawingCodeMapper;
import com.example.Dynamo_Backend.mapper.OrderDrawingCodeMapper;
import com.example.Dynamo_Backend.mapper.OrderMapper;
import com.example.Dynamo_Backend.service.DrawingCodeService;
import com.example.Dynamo_Backend.service.OrderDrawingCodeService;
import com.example.Dynamo_Backend.service.OrderService;
import com.example.Dynamo_Backend.repository.*;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderDrawingCodeImplementation implements OrderDrawingCodeService {
    public OrderDrawingCodeRepository orderDrawingCodeRepository;
    public OrderService orderService;
    public DrawingCodeService drawingCodeService;

    @Override
    public OrderDrawingCodeDto addOrderDrawingCode(OrderDrawingCodeDto orderDrawingCodeDto) {
        OrderDrawingCode orderDrawingCode = OrderDrawingCodeMapper.mapToOrderDrawingCode(orderDrawingCodeDto);
        OrderDto order = orderService.getOrderById(orderDrawingCodeDto.getOrderId());
        DrawingCodeDto DrawingCode = drawingCodeService.getDrawingCodeById(orderDrawingCodeDto.getDrawingCodeId());
        DrawingCode newDrawingCode = DrawingCodeMapper.mapToDrawingCode(DrawingCode);
        Order newOrder = OrderMapper.mapToOrder(order);
        long createdTimestamp = System.currentTimeMillis();

        orderDrawingCode.setDrawingCode(newDrawingCode);
        orderDrawingCode.setOrder(newOrder);
        orderDrawingCode.setCreatedDate(createdTimestamp);
        orderDrawingCode.setUpdatedDate(createdTimestamp);

        OrderDrawingCode saveOrderDrawingCode = orderDrawingCodeRepository.save(orderDrawingCode);
        return OrderDrawingCodeMapper.mapToOrderDrawingCodeDto(saveOrderDrawingCode);
    }

    @Override
    public OrderDrawingCodeDto updateOrderDrawingCode(String Id, OrderDrawingCodeDto orderDrawingCodeDto) {
        OrderDrawingCode orderDrawingCode = orderDrawingCodeRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("OrderDrawingCode is not found:" + Id));
        DrawingCodeDto drawingCode = drawingCodeService.getDrawingCodeById(orderDrawingCodeDto.getDrawingCodeId());
        OrderDto order = orderService.getOrderById(orderDrawingCodeDto.getOrderId());
        DrawingCode updateDrawingCode = DrawingCodeMapper.mapToDrawingCode(drawingCode);
        long updatedTimestamp = System.currentTimeMillis();

        Order updateOrder = OrderMapper.mapToOrder(order);
        orderDrawingCode.setOrder(updateOrder);
        orderDrawingCode.setDrawingCode(updateDrawingCode);
        orderDrawingCode.setUpdatedDate(updatedTimestamp);
        OrderDrawingCode updatedOrderDrawingCode = orderDrawingCodeRepository.save(orderDrawingCode);
        return OrderDrawingCodeMapper.mapToOrderDrawingCodeDto(updatedOrderDrawingCode);
    }

    @Override
    public OrderDrawingCodeDto getOrderDrawingCodeById(String Id) {
        OrderDrawingCode orderDrawingCode = orderDrawingCodeRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("OrderDrawingCode is not found:" + Id));
        return OrderDrawingCodeMapper.mapToOrderDrawingCodeDto(orderDrawingCode);
    }

    @Override
    public void deleteOrderDrawingCode(String Id) {
        OrderDrawingCode orderDrawingCode = orderDrawingCodeRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("OrderDrawingCode is not found:" + Id));
        orderDrawingCodeRepository.delete(orderDrawingCode);
    }

    @Override
    public List<OrderDrawingCodeDto> getOrderDrawingCodes() {
        List<OrderDrawingCode> orderDrawingCodes = orderDrawingCodeRepository.findAll();
        return orderDrawingCodes.stream().map(OrderDrawingCodeMapper::mapToOrderDrawingCodeDto).toList();
    }

}
