package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.ProcessTimeSummaryDto;
import com.example.Dynamo_Backend.entities.ProcessTimeSummary;

public class ProcessTimeSummaryMapper {
    public static ProcessTimeSummaryDto toDto(ProcessTimeSummary entity) {
        return new ProcessTimeSummaryDto(
                entity.getId(),
                entity.getQuantity(),
                entity.getProductionStep(),
                entity.getManufacturingPoint(),
                entity.getPgTime(),
                entity.getSpanTime(),
                entity.getRunTime(),
                entity.getStopTime(),
                entity.getOffsetTime()
        // entity.getOrderDetail() != null ? entity.getOrderDetail().getOrderCode() :
        // null
        );
    }

    public static ProcessTimeSummary toEntity(ProcessTimeSummaryDto dto) {
        ProcessTimeSummary entity = new ProcessTimeSummary();
        entity.setId(dto.getId());
        entity.setQuantity(dto.getQuantity());
        entity.setProductionStep(dto.getProductionStep());
        entity.setManufacturingPoint(dto.getManufacturingPoint());
        entity.setPgTime(dto.getPgTime());
        entity.setSpanTime(dto.getSpanTime());
        entity.setRunTime(dto.getRunTime());
        entity.setStopTime(dto.getStopTime());
        entity.setOffsetTime(dto.getOffsetTime());
        // Set OrderDetail in service
        return entity;
    }
}