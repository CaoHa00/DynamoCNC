package com.example.Dynamo_Backend.dto.ResponseDto;

public record HistoryProcessFlatDto(
        String processId,
        String orderCode,
        Integer partNumber,
        Integer stepNumber,
        Long startTime,
        Long endTime,
        String machineName,
        Integer staffId,
        String shortName) {

}