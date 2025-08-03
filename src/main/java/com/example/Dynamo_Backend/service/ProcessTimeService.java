package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.ProcessTimeDto;

public interface ProcessTimeService {
    ProcessTimeDto addProcessTime(ProcessTimeDto processTimeDto);

    ProcessTimeDto updateProcessTime(Integer processTimeId, ProcessTimeDto processTimeDto);

    ProcessTimeDto getProcessTimeById(Integer processTimeId);

    void deleteProcessTime(Integer processTimeId);

    List<ProcessTimeDto> getAllProcessTime();
}
