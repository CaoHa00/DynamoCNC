package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.OperateHistoryDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;

public interface OperateHistoryService {
    void addOperateHistory(String payload);

    void handleOperate(Integer machineId, DrawingCodeProcess drawingCodeProcess);

    // OperateHistoryDto addOperateHistory(OperateHistoryDto operateHistoryDto);

    // OperateHistoryDto updateOperateHistory(String Id, OperateHistoryDto
    // operateHistoryDto);

    OperateHistoryDto getOperateHistoryById(String Id);

    void deleteOperateHistory(String Id);

    List<OperateHistoryDto> getAllOperateHistory();

}
