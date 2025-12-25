package com.example.Dynamo_Backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.dto.ResponseDto.ListOrderDetailStatus;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderDetailResponseDto;

public interface OrderDetailService {
    OrderDetailDto addOrderDetail(OrderDetailDto orderDetailDto);

    OrderDetailDto updateOrderDetail(String Id, OrderDetailDto orderDetailDto);

    OrderDetailDto getOrderDetailById(String Id);

    void deleteOrderDetail(String Id);

    Page<OrderDetailResponseDto> getOrderDetails(int page, int size);

    List<OrderDetailResponseDto> getOrderDetails();

    void updateOrderCode(String drawingCodeId, String orderId);

    void importExcel(MultipartFile file);

    List<ListOrderDetailStatus> getListOrderStatus();
}
