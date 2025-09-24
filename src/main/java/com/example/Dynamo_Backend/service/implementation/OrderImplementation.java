package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.Dynamo_Backend.dto.OrderDto;
import com.example.Dynamo_Backend.entities.Order;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Order is not found:" + orderId));

        long updatedTimestamp = System.currentTimeMillis();

        order.setUpdatedDate(updatedTimestamp);

        order.setPoNumber(orderDto.getPoNumber());

        order.setStatus(orderDto.getStatus());

        Order updateOrder = orderRepository.save(order);

        // orderDetailService.updateOrderCode("", orderId);
        return OrderMapper.mapToOrderDto(updateOrder);
    }

    @Override
    public OrderDto getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order is not found:" + orderId));
        return OrderMapper.mapToOrderDto(order);
    }

    @Override
    public void deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order is not found:" + orderId));
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
            List<Order> orders = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() < 6)
                    continue;
                Order order = new Order();
                Cell poNumberCell = row.getCell(2);
                if (poNumberCell == null)
                    continue;
                if (poNumberCell.getCellType() == CellType.NUMERIC) {
                    order.setPoNumber(String.format("%.0f", poNumberCell.getNumericCellValue()));
                } else {
                    order.setPoNumber(poNumberCell.getStringCellValue());
                }
                order.setStatus(1);
                orders.add(order);
            }
            orderRepository.saveAll(orders);
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new BusinessException("Failed to import orders from Excel file: " + e.getMessage());
        }
    }

    @Override
    public List<OrderDto> getAllActiveOrder() {
        List<Order> orders = orderRepository.findAllByStatus(1);
        return orders.stream().map(OrderMapper::mapToOrderDto).toList();
    }

}
