package com.example.Dynamo_Backend.service.implementation;

import com.example.Dynamo_Backend.dto.ProcessTimeSummaryDto;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.entities.ProcessTimeSummary;
import com.example.Dynamo_Backend.mapper.ProcessTimeSummaryMapper;
import com.example.Dynamo_Backend.repository.OrderDetailRepository;
import com.example.Dynamo_Backend.repository.ProcessTimeRepository;
import com.example.Dynamo_Backend.repository.ProcessTimeSummaryRepository;
import com.example.Dynamo_Backend.service.ProcessTimeSummaryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProcessTimeSummaryImplementation implements ProcessTimeSummaryService {

    private final ProcessTimeSummaryRepository repository;
    private final ProcessTimeRepository processTimeRepository;

    @Override
    public List<ProcessTimeSummaryDto> getAll() {
        return repository.findAll().stream()
                .map(ProcessTimeSummaryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProcessTimeSummaryDto getByOrderCode(String orderCode) {
        ProcessTimeSummary summary = repository.findByOrderDetail_OrderCode(orderCode).orElse(null);
        if (summary == null) {
            return new ProcessTimeSummaryDto(0, 0, 0, 0f, 0f, 0f, 0f, 0f, 0f, null);
        }
        return ProcessTimeSummaryMapper.toDto(summary);
    }

    @Override
    public ProcessTimeSummaryDto sumTimesByOrderCode(String orderCode) {
        List<ProcessTime> processTimes = processTimeRepository
                .findAllByDrawingCodeProcess_OrderDetail_OrderCode(orderCode);
        int quantity = processTimes.stream()
                .mapToInt(pt -> pt.getDrawingCodeProcess().getPartNumber())
                .max()
                .orElse(0);
        float totalSpanTime = 0f;
        float totalRunTime = 0f;
        float totalPgTime = 0f;
        float totalStopTime = 0f;
        float totalOffsetTime = 0f;
        float manufacturing_point = 0f;

        for (ProcessTime pt : processTimes) {
            totalSpanTime += pt.getSpanTime();
            totalRunTime += pt.getRunTime();
            totalPgTime += pt.getPgTime();
            totalStopTime += pt.getStopTime();
            totalOffsetTime += pt.getOffsetTime();
            manufacturing_point += pt.getDrawingCodeProcess().getManufacturingPoint();
        }
        ProcessTimeSummary summary = repository.findByOrderDetail_OrderCode(orderCode).orElse(null);
        if (summary == null) {
            summary = new ProcessTimeSummary();
        }
        summary.setQuantity(quantity);
        summary.setManufacturingPoint(manufacturing_point);
        summary.setPgTime(totalPgTime);
        summary.setSpanTime(totalSpanTime);
        summary.setRunTime(totalRunTime);
        summary.setStopTime(totalStopTime);
        summary.setOffsetTime(totalOffsetTime);
        summary.setProductionStep(processTimes.isEmpty() ? null : processTimes.size());
        summary.setOrderDetail(processTimes.get(0).getDrawingCodeProcess().getOrderDetail());
        ProcessTimeSummary savedSummary = repository.save(summary);
        return ProcessTimeSummaryMapper.toDto(savedSummary);
    }

}