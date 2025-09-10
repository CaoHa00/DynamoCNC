package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderCodeOverviewDto;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderDetailStatisticDto;

public interface OrderDetailStatisticService {
    OrderDetailStatisticDto getOrderDetailStatistics(GroupEfficiencyRequestDto request);

    List<OrderCodeOverviewDto> getOrderCodeOverview(GroupEfficiencyRequestDto request);
}
