package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.service.OrderDetailService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/order-detail")
public class OrderDetailController {
    public final OrderDetailService orderDetailService;

    @GetMapping
    public ResponseEntity<List<OrderDetailDto>> getAllOrderDetails() {
        List<OrderDetailDto> orderDetails = orderDetailService.getOrderDetails();
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
}
