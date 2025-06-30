package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.OperateHistoryDto;
import com.example.Dynamo_Backend.dto.OperatorDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.Operator;
import com.example.Dynamo_Backend.mapper.DrawingCodeProcessMapper;

import com.example.Dynamo_Backend.mapper.OperateHistoryMapper;
import com.example.Dynamo_Backend.mapper.OperatorMapper;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.service.DrawingCodeProcessService;
import com.example.Dynamo_Backend.service.OperateHistoryService;
import com.example.Dynamo_Backend.service.OperatorService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OperateHistoryImplementation implements OperateHistoryService {
        DrawingCodeProcessService drawingCodeProcessService;
        OperatorService operatorService;
        OperateHistoryRepository operateHistoryRepository;

        @Override
        public OperateHistoryDto addOperateHistory(OperateHistoryDto operateHistoryDto) {
                DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService
                                .getDrawingCodeProcessById(operateHistoryDto.getDrawingCodeProcessId());
                DrawingCodeProcess newdrawingCodeProcess = DrawingCodeProcessMapper
                                .mapToDrawingCodeProcess(drawingCodeProcess);
                OperatorDto operator = operatorService.getOperatorById(operateHistoryDto.getOperateHistoryId());
                Operator newOperator = OperatorMapper.mapToOperator(operator);

                OperateHistory operateHistory = OperateHistoryMapper
                                .mapToOperateHistory(operateHistoryDto);
                operateHistory.setOperator(newOperator);

                operateHistory.setDrawingCodeProcess(newdrawingCodeProcess);

                OperateHistory saveOperateHistory = operateHistoryRepository.save(operateHistory);
                return OperateHistoryMapper.mapToOperateHistoryDto(saveOperateHistory);
        }

        @Override
        public OperateHistoryDto updateOperateHistory(String Id, OperateHistoryDto operateHistoryDto) {
                OperateHistory operateHistory = operateHistoryRepository.findById(Id)
                                .orElseThrow(() -> new RuntimeException("DrawingCode is not found:" + Id));
                DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService
                                .getDrawingCodeProcessById(operateHistoryDto.getDrawingCodeProcessId());
                DrawingCodeProcess updateDrawingCodeProcess = DrawingCodeProcessMapper
                                .mapToDrawingCodeProcess(drawingCodeProcess);
                OperatorDto operator = operatorService.getOperatorById(operateHistoryDto.getOperateHistoryId());
                Operator updateOperator = OperatorMapper.mapToOperator(operator);

                operateHistory.setOperator(updateOperator);
                operateHistory.setManufacturingPoint(operateHistoryDto.getManufacturingPoint());
                operateHistory.setStartTime(operateHistoryDto.getStartTime());
                operateHistory.setStopTime(operateHistoryDto.getStopTime());
                operateHistory.setDrawingCodeProcess(updateDrawingCodeProcess);

                OperateHistory updateOperateHistory = operateHistoryRepository.save(operateHistory);
                return OperateHistoryMapper.mapToOperateHistoryDto(updateOperateHistory);
        }

        @Override
        public OperateHistoryDto getOperateHistoryById(String Id) {
                OperateHistory operateHistory = operateHistoryRepository.findById(Id)
                                .orElseThrow(() -> new RuntimeException("OperateHistory is not found:" + Id));
                return OperateHistoryMapper.mapToOperateHistoryDto(operateHistory);
        }

        @Override
        public void deleteOperateHistory(String Id) {
                OperateHistory operateHistory = operateHistoryRepository.findById(Id)
                                .orElseThrow(() -> new RuntimeException("DrawingCode is not found:" + Id));
                operateHistoryRepository.delete(operateHistory);
        }

        @Override
        public List<OperateHistoryDto> getAllOperateHistory() {
                List<OperateHistory> operateHistories = operateHistoryRepository.findAll();
                return operateHistories.stream().map(OperateHistoryMapper::mapToOperateHistoryDto).toList();
        }

}
