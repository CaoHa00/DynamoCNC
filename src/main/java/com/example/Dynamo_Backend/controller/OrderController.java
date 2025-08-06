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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.OrderDto;
import com.example.Dynamo_Backend.service.OrderService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {
    public final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrder();
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @PostMapping
    public ResponseEntity<OrderDto> addOrder(@RequestBody OrderDto orderDto) {
        OrderDto order = orderService.addOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);

    }

    @PutMapping("/{order_id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable("order_id") String Id,
            @RequestBody OrderDto orderDto) {
        OrderDto updateOrders = orderService.updateOrder(Id, orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateOrders);
    }

    @DeleteMapping("/{order_id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("order_id") String Id) {
        orderService.deleteOrder(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{order_id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("order_id") String Id) {
        OrderDto orders = orderService.getOrderById(Id);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadOrderExcel(@RequestParam("file") MultipartFile file) {
        orderService.importOrderFromExcel(file);
        return ResponseEntity.ok().build();
    }
}
