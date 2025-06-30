package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;

public interface DrawingCodeProcessService {
    DrawingCodeProcessDto addDrawingCodeProcess(DrawingCodeProcessDto drawingCodeProcessDto);

    DrawingCodeProcessResponseDto updateDrawingCodeProcess(String drawingCodeProcessId,
            DrawingCodeProcessDto drawingCodeProcessDto);

    DrawingCodeProcessDto getDrawingCodeProcessById(String drawingCodeProcessId);

    void deleteDrawingCodeProcess(String drawingCodeProcessId);

    List<DrawingCodeProcessDto> getAllDrawingCodeProcess();

    List<DrawingCodeProcessResponseDto> getAll();

    void recieveProcessFromTablet(String drawingCodeProcessId, Integer machineId, String operatorId);

}
