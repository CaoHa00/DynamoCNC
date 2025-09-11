package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.*;
import com.example.Dynamo_Backend.service.OrderDetailStatisticService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/order-detail-statistic")
public class OrderDetailStatisticController {

    private final OrderDetailStatisticService orderDetailStatisticService;

    @PostMapping("/statistic")
    public ResponseEntity<OrderDetailStatisticDto> getOrderDetailStatistics(
            @RequestBody GroupEfficiencyRequestDto request) {
        OrderDetailStatisticDto statistics = orderDetailStatisticService.getOrderDetailStatistics(request);
        return ResponseEntity.status(HttpStatus.OK).body(statistics);
    }

    @PostMapping("/overview")
    public ResponseEntity<List<OrderCodeOverviewDto>> getOrderDetailOverviews(
            @RequestBody GroupEfficiencyRequestDto request) {
        List<OrderCodeOverviewDto> overviews = orderDetailStatisticService.getOrderCodeOverview(request);
        return ResponseEntity.status(HttpStatus.OK).body(overviews);
    }
}
