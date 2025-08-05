package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.Dynamo_Backend.dto.OrderDto;
import com.example.Dynamo_Backend.entities.Order;
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

        order.setCreatedDate(createdTimestamp);
        order.setUpdatedDate(createdTimestamp);

        Order saveOrder = orderRepository.save(order);
        return OrderMapper.mapToOrderDto(saveOrder);
    }

    @Override
    public OrderDto updateOrder(String orderId, OrderDto orderDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order is not found:" + orderId));

        long updatedTimestamp = System.currentTimeMillis();

        order.setUpdatedDate(updatedTimestamp);

        order.setPoNumber(orderDto.getPoNumber());

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

    @Override
    public void importOrderFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = ((MultipartFile) file).getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;
                OrderDto orderDto = new OrderDto();
                orderDto.setPoNumber(row.getCell(0).getStringCellValue());
                addOrder(orderDto);
            }
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to import orders from Excel file", e);
        }
    }

}
