package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.dto.OrderDto;
import com.example.Dynamo_Backend.entities.DrawingCode;
import com.example.Dynamo_Backend.entities.Order;
import com.example.Dynamo_Backend.mapper.DrawingCodeMapper;
import com.example.Dynamo_Backend.mapper.OrderMapper;
import com.example.Dynamo_Backend.repository.OrderRepository;
import com.example.Dynamo_Backend.service.DrawingCodeService;
import com.example.Dynamo_Backend.service.OrderService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderImplementation implements OrderService {
    DrawingCodeService drawingCodeService;
    OrderRepository orderRepository;

    @Override
    public OrderDto addOrder(OrderDto orderDto) {
        Order order = OrderMapper.mapToOrder(orderDto);
        long createdTimestamp = System.currentTimeMillis();

        DrawingCodeDto drawingCode = drawingCodeService.getDrawingCodeById(orderDto.getDrawingCodeId());
        DrawingCode newDrawingCode = DrawingCodeMapper.mapToDrawingCode(drawingCode);

        order.setDrawingCode(newDrawingCode);

        order.setCreatedDate(createdTimestamp);
        order.setUpdatedDate(createdTimestamp);

        Order saveOrder = orderRepository.save(order);
        return OrderMapper.mapToOrderDto(saveOrder);
    }

    @Override
    public OrderDto updateOrder(String orderId, OrderDto orderDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order is not found:" + orderId));

        DrawingCodeDto drawingCode = drawingCodeService.getDrawingCodeById(orderDto.getDrawingCodeId());
        DrawingCode newDrawingCode = DrawingCodeMapper.mapToDrawingCode(drawingCode);

        long updatedTimestamp = System.currentTimeMillis();

        order.setUpdatedDate(updatedTimestamp);

        order.setPoNumber(orderDto.getPoNumber());
        order.setDrawingCode(newDrawingCode);

        Order updateOrder = orderRepository.save(order);
        return OrderMapper.mapToOrderDto(updateOrder);
    }

    @Override
    public OrderDto getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order is not found:" + orderId));
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    public void deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order is not found:" + orderId));
        orderRepository.delete(order);
    }

    @Override
    public List<OrderDto> getAllOrder() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(OrderMapper::mapToOrderDto).toList();
    }

}
