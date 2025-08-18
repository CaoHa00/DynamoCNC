package com.example.Dynamo_Backend.service;

import com.example.Dynamo_Backend.dto.ProcessTimeSummaryDto;

import java.util.List;

public interface ProcessTimeSummaryService {
    List<ProcessTimeSummaryDto> getAll();

    ProcessTimeSummaryDto getByOrderDetailId(String orderDetailId);

    ProcessTimeSummaryDto sumTimesByOrderDetailId(String orderDetailId);
}