package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.dto.ResponseDto.ListOrderDetailStatus;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderDetailResponseDto;
import com.example.Dynamo_Backend.service.OrderDetailService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/order-detail")
public class OrderDetailController {
    public final OrderDetailService orderDetailService;

    @GetMapping
    public ResponseEntity<Page<OrderDetailResponseDto>> getAllOrderDetails(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderDetailResponseDto> orderDetails = orderDetailService.getOrderDetails(keyword, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(orderDetails);
    }

    @GetMapping("/list")
    public ResponseEntity<List<OrderDetailResponseDto>> getAllOrderDetails() {
        List<OrderDetailResponseDto> orderDetails = orderDetailService.getOrderDetails();
        return ResponseEntity.status(HttpStatus.OK).body(orderDetails);
    }

    @PostMapping
    public ResponseEntity<OrderDetailDto> addOrderDetail(@RequestBody OrderDetailDto orderDetailDto) {
        OrderDetailDto orderDetail = orderDetailService.addOrderDetail(orderDetailDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetail);

    }

    @PutMapping("/{orderDetail_id}")
    public ResponseEntity<OrderDetailDto> updateOrderDetail(@PathVariable("orderDetail_id") String Id,
            @RequestBody OrderDetailDto orderDetailDto) {
        OrderDetailDto updateOrderDetails = orderDetailService.updateOrderDetail(Id, orderDetailDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateOrderDetails);
    }

    @DeleteMapping("/{orderDetail_id}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable("orderDetail_id") String Id) {
        orderDetailService.deleteOrderDetail(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderDetail_id}")
    public ResponseEntity<OrderDetailDto> getOrderDetailById(@PathVariable("orderDetail_id") String Id) {
        OrderDetailDto orderDetails = orderDetailService.getOrderDetailById(Id);
        return ResponseEntity.ok(orderDetails);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadMonthlyGroupKpiExcel(@RequestParam("file") MultipartFile file) {
        orderDetailService.importExcel(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orderStatus")
    public ResponseEntity<Page<ListOrderDetailStatus>> getOrderStatus(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ListOrderDetailStatus> dto = orderDetailService.getListOrderStatus(pageable, keyword);
        return ResponseEntity.ok(dto);
    }
}
