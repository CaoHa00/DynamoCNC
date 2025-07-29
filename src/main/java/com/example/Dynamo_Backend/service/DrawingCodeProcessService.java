package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.RequestDto.DrawingCodeProcessResquestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;

public interface DrawingCodeProcessService {
    DrawingCodeProcessDto addDrawingCodeProcess(DrawingCodeProcessResquestDto drawingCodeProcessDto);

    DrawingCodeProcessResponseDto updateDrawingCodeProcess(String drawingCodeProcessId,
            DrawingCodeProcessResquestDto drawingCodeProcessDto);

    DrawingCodeProcessDto getDrawingCodeProcessById(String drawingCodeProcessId);

    DrawingCodeProcessDto getDrawingCodeProcessByMachineId(Integer machineId);

    void deleteDrawingCodeProcess(String drawingCodeProcessId);

    List<DrawingCodeProcessDto> getAllDrawingCodeProcess();

    List<DrawingCodeProcessResponseDto> getAll();

    void receiveProcessFromTablet(String drawingCodeProcessId, Integer machineId, String staffId);

    DrawingCodeProcessDto addProcessByOperator(DrawingCodeProcessResquestDto drawingCodeProcessDto);

    void doneProcess(String processId);

}
