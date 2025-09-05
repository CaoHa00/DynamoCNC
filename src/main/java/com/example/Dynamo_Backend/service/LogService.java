package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.LogDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.Staff;

public interface LogService {
    void addLog(CurrentStatus currrentStatus, Machine machine, Staff staff);

    // LogDto updateLog(String statsId, LogDto statsDto);

    LogDto getLogById(String statsId);

    void deleteLog(String statsId);

    List<LogDto> getAllLog();
}
