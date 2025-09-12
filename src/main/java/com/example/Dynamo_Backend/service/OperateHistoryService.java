package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.OperateHistoryDto;

public interface OperateHistoryService {
    OperateHistoryDto addOperateHistory(String payload);

    // OperateHistoryDto addOperateHistory(OperateHistoryDto operateHistoryDto);

    // OperateHistoryDto updateOperateHistory(String Id, OperateHistoryDto
    // operateHistoryDto);

    OperateHistoryDto getOperateHistoryById(String Id);

    void deleteOperateHistory(String Id);

    List<OperateHistoryDto> getAllOperateHistory();

}
