package com.example.Dynamo_Backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.RequestDto.DrawingCodeProcessResquestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;

public interface DrawingCodeProcessService {
        DrawingCodeProcessDto addDrawingCodeProcess(DrawingCodeProcessResquestDto drawingCodeProcessDto);

        void addDrawingCodeProcess(List<DrawingCodeProcessResquestDto> drawingCodeProcessDto);

        DrawingCodeProcessResponseDto updateDrawingCodeProcess(String drawingCodeProcessId,
                        DrawingCodeProcessResquestDto drawingCodeProcessDto);

        DrawingCodeProcessResponseDto updateProcessByOperator(String drawingCodeProcessId,
                        DrawingCodeProcessResquestDto drawingCodeProcessDto);

        DrawingCodeProcessDto getDrawingCodeProcessById(String drawingCodeProcessId);

        Map<String, Object> getDrawingCodeProcessByMachineId(Integer machineId);
        // List<DrawingCodeProcessResponseDto> getDrawingCodeProcessByMachineId(Integer
        // machineId);

        DrawingCodeProcessDto getProcessDtoByMachineId(Integer machineId);

        void deleteDrawingCodeProcess(String drawingCodeProcessId);

        List<DrawingCodeProcessDto> getAllDrawingCodeProcess();

        List<DrawingCodeProcessResponseDto> getAllTodoProcesses();

        List<DrawingCodeProcessResponseDto> getAll();

        List<DrawingCodeProcessResponseDto> getPlannedProcesses(Integer planned);

        void receiveProcessFromTablet(String drawingCodeProcessId, Integer machineId, String staffId);

        DrawingCodeProcessDto addProcessByOperator(DrawingCodeProcessResquestDto drawingCodeProcessDto);

        void doneProcess(String processId);

        DrawingCodeProcessResponseDto updateProcessByAdmin(String drawingCodeProcessId,
                        DrawingCodeProcessResquestDto drawingCodeProcessDto);

        List<DrawingCodeProcessResponseDto> getCompletedProcess(Integer status, Long start, Long stop);

        List<DrawingCodeProcessResponseDto> getProcessesByOperator(String staffId, Long start, Long stop);

        List<DrawingCodeProcessResponseDto> getProcessByMachine(Integer machineId, Long start, Long stop);

        List<DrawingCodeProcessResponseDto> getCompletedProcessWithOperateHistoryData(String staffId, Long start,
                        Long stop);

        void importExcel(MultipartFile file);

        DrawingCodeProcessDto updateDrawingCodeProcess(DrawingCodeProcessResquestDto drawingCodeProcessDto);

        List<DrawingCodeProcessDto> getProcessesByOrderDetail(String orderDetailId);

        void updateProcessStatus(String orderCode);

}
