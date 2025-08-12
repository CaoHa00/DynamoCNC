package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.ProcessTimeDto;
import com.example.Dynamo_Backend.entities.ProcessTime;

public class ProcessTimeMapper {
    public static ProcessTime mapToProcessTime(ProcessTimeDto processTimeDto) {
        ProcessTime processTime = new ProcessTime();
        processTime.setId(processTimeDto.getId());
        processTime.setOffsetTime(processTimeDto.getOffsetTime());
        processTime.setPgTime(processTimeDto.getPgTime());
        processTime.setRunTime(processTimeDto.getRunTime());
        processTime.setSpanTime(processTimeDto.getSpanTime());
        processTime.setStopTime(processTimeDto.getStopTime());
        return processTime;
    }

    public static ProcessTimeDto mapToProcessTimeDto(ProcessTime processTime) {
        ProcessTimeDto processTimeDto = new ProcessTimeDto();
        processTimeDto.setId(processTime.getId());
        processTimeDto.setOffsetTime(processTime.getOffsetTime());
        processTimeDto.setPgTime(processTime.getPgTime());
        processTimeDto.setRunTime(processTime.getRunTime());
        processTimeDto.setSpanTime(processTime.getSpanTime());
        processTimeDto.setStopTime(processTime.getStopTime());
        processTimeDto.setDrawingCodeProcessId(processTime.getDrawingCodeProcess().getProcessId());
        return processTimeDto;
    }
}
