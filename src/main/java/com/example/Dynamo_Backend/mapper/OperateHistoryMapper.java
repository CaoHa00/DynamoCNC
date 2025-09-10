package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.OperateHistoryDto;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class OperateHistoryMapper {
    public static OperateHistoryDto mapToOperateHistoryDto(OperateHistory operateHistory) {
        return new OperateHistoryDto(
                operateHistory.getOperateHistoryId(),
                operateHistory.getManufacturingPoint(),
                operateHistory.getPgTime(),
                DateTimeUtil.convertTimestampToString(operateHistory.getStartTime()),
                DateTimeUtil.convertTimestampToString(operateHistory.getStopTime()),
                operateHistory.getInProgress(),
                operateHistory.getStaff() != null ? operateHistory.getStaff().getId()
                        : null,
                operateHistory.getDrawingCodeProcess() != null ? operateHistory.getDrawingCodeProcess().getProcessId()
                        : null);
    }

    public static OperateHistory mapToOperateHistory(OperateHistoryDto operateHistoryDto) {
        OperateHistory operateHistory = new OperateHistory();
        operateHistory.setOperateHistoryId(operateHistoryDto.getOperateHistoryId());
        operateHistory.setManufacturingPoint(operateHistoryDto.getManufacturingPoint());
        operateHistory.setStartTime(DateTimeUtil.convertStringToTimestamp(operateHistoryDto.getStartTime()));
        operateHistory.setStopTime(DateTimeUtil.convertStringToTimestamp(operateHistoryDto.getStopTime()));
        operateHistory.setInProgress(operateHistoryDto.getInProgress());
        return operateHistory;
    }
}
