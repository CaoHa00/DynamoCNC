package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.OperateHistoryDto;
import com.example.Dynamo_Backend.entities.OperateHistory;

public class OperateHistoryMapper {
    public static OperateHistoryDto mapToOperateHistoryDto(OperateHistory operateHistory) {
        return new OperateHistoryDto(
                operateHistory.getOperateHistoryId(),
                operateHistory.getManufacturingPoint(),
                operateHistory.getStartTime(),
                operateHistory.getStopTime(),
                operateHistory.getOperator() != null ? operateHistory.getOperator().getId()
                        : null,
                operateHistory.getDrawingCodeProcess() != null ? operateHistory.getDrawingCodeProcess().getProcessId()
                        : null);
    }

    public static OperateHistory mapToOperateHistory(OperateHistoryDto operateHistoryDto) {
        OperateHistory operateHistory = new OperateHistory();
        operateHistory.setOperateHistoryId(operateHistoryDto.getOperateHistoryId());
        operateHistory.setManufacturingPoint(operateHistoryDto.getManufacturingPoint());
        operateHistory.setStartTime(operateHistoryDto.getStartTime());
        operateHistory.setStopTime(operateHistoryDto.getStopTime());
        return operateHistory;
    }
}
