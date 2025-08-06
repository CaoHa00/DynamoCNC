package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.dto.OrderDto;
import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.entities.DrawingCode;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.entities.Order;
import com.example.Dynamo_Backend.mapper.DrawingCodeMapper;
import com.example.Dynamo_Backend.mapper.OrderDetailMapper;
import com.example.Dynamo_Backend.mapper.OrderMapper;
import com.example.Dynamo_Backend.service.DrawingCodeService;
import com.example.Dynamo_Backend.service.OrderDetailService;
import com.example.Dynamo_Backend.service.OrderService;
import com.example.Dynamo_Backend.repository.*;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderDetailImplementation implements OrderDetailService {
    public OrderDetailRepository orderDetailRepository;
    public OrderService orderService;
    public DrawingCodeService drawingCodeService;

    @Override
    public OrderDetailDto addOrderDetail(OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = OrderDetailMapper.mapToOrderDetail(orderDetailDto);
        OrderDto order = orderService.getOrderById(orderDetailDto.getOrderId());
        DrawingCodeDto DrawingCode = drawingCodeService.getDrawingCodeById(orderDetailDto.getDrawingCodeId());
        DrawingCode newDrawingCode = DrawingCodeMapper.mapToDrawingCode(DrawingCode);
        Order newOrder = OrderMapper.mapToOrder(order);
        long createdTimestamp = System.currentTimeMillis();
        String orderCode = newOrder.getPoNumber() + "_" + newDrawingCode.getDrawingCodeName();
        orderDetail.setOrderCode(orderCode);
        orderDetail.setDrawingCode(newDrawingCode);
        orderDetail.setOrder(newOrder);
        orderDetail.setCreatedDate(createdTimestamp);
        orderDetail.setUpdatedDate(createdTimestamp);
        OrderDetail saveOrderDetail = orderDetailRepository.save(orderDetail);
        return OrderDetailMapper.mapToOrderDetailDto(saveOrderDetail);
    }

    @Override
    public OrderDetailDto updateOrderDetail(String Id, OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = orderDetailRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("OrderD    etail is not found:" + Id));
        DrawingCodeDto drawingCode = drawingCodeService.getDrawingCodeById(orderDetailDto.getDrawingCodeId());
        OrderDto order = orderService.getOrderById(orderDetailDto.getOrderId());
        DrawingCode updateDrawingCode = DrawingCodeMapper.mapToDrawingCode(drawingCode);
        long updatedTimestamp = System.currentTimeMillis();
        if (orderDetailDto.getCreatedDate() == null) {
            orderDetail.setCreatedDate(orderDetail.getCreatedDate());
        }
        Order updateOrder = OrderMapper.mapToOrder(order);
        orderDetail.setOrder(updateOrder);
        orderDetail.setDrawingCode(updateDrawingCode);
        orderDetail.setUpdatedDate(updatedTimestamp);
        orderDetail.setQuantity(orderDetailDto.getQuantity());
        orderDetail.setOrderCode(updateOrder.getPoNumber() + "_" + updateDrawingCode.getDrawingCodeName());
        orderDetail.setOrderType(orderDetailDto.getOrderType());
        OrderDetail updatedOrderDetail = orderDetailRepository.save(orderDetail);
        return OrderDetailMapper.mapToOrderDetailDto(updatedOrderDetail);
    }

    @Override
    public OrderDetailDto getOrderDetailById(String Id) {
        OrderDetail orderDetail = orderDetailRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("OrderDetail is not found:" + Id));
        return OrderDetailMapper.mapToOrderDetailDto(orderDetail);
    }

    @Override
    public void deleteOrderDetail(String Id) {
        OrderDetail orderDetail = orderDetailRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("OrderDetail is not found:" + Id));
        orderDetailRepository.delete(orderDetail);
    }

    @Override
    public List<OrderDetailDto> getOrderDetails() {
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        return orderDetails.stream().map(OrderDetailMapper::mapToOrderDetailDto).toList();
    }

    @Override
    public void updateOrderCode(String drawingCodeId, String orderId) {
        // OrderDetail orderDetail = null;
        // String drawingCodeName = "";
        // String poNumber = "";
        // if ((drawingCodeId != null)) {
        // orderDetail =
        // orderDetailRepository.findByDrawingCode(drawingCodeId).orElse(null);
        // drawingCodeName = orderDetail.getDrawingCode().getDrawingCodeName();
        // poNumber = orderDetail.getOrder().getPoNumber();
        // } else {
        // orderDetail = orderDetailRepository.findByOrder(orderId).orElse(null);
        // drawingCodeName = orderDetail.getDrawingCode().getDrawingCodeName();
        // poNumber = orderDetail.getOrder().getPoNumber();
        // }

        // orderDetail.setOrderCode(poNumber + "_" + drawingCodeName);
    }

}
