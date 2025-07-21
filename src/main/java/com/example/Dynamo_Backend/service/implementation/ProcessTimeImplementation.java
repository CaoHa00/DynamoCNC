package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.ProcessTimeDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.mapper.ProcessTimeMapper;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.ProcessTimeRepository;
import com.example.Dynamo_Backend.service.ProcessTimeService;

@Service
public class ProcessTimeImplementation implements ProcessTimeService {
    @Autowired
    ProcessTimeRepository processTimeRepository;
    @Autowired
    DrawingCodeProcessRepository drawingCodeProcessRepository;

    @Override
    public ProcessTimeDto addProcessTime(ProcessTimeDto processTimeDto) {
        ProcessTime processTime = ProcessTimeMapper.mapToProcessTime(processTimeDto);
        ProcessTime savedProcessTime = processTimeRepository.save(processTime);
        return ProcessTimeMapper.mapToProcessTimeDto(savedProcessTime);
    }

    @Override
    public ProcessTimeDto updateProcessTime(Integer processTimeId, ProcessTimeDto processTimeDto) {
        ProcessTime processTime = processTimeRepository.findById(processTimeId)
                .orElseThrow(() -> new RuntimeException("ProcessTime is not found:" + processTimeId));
        DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository
                .findById(processTimeDto.getDrawingCodeProcessId())
                .orElseThrow(() -> new RuntimeException(
                        "DrawingCodeProcess is not found:" + processTimeDto.getDrawingCodeProcessId()));

        processTime.setDrawingCodeProcess(drawingCodeProcess);
        processTime.setOffsetTime(processTimeDto.getOffsetTime());
        processTime.setPgTime(processTimeDto.getPgTime());
        processTime.setRunTime(processTimeDto.getRunTime());
        processTime.setSpanTime(processTimeDto.getSpanTime());
        processTime.setStopTime(processTimeDto.getStopTime());

        ProcessTime updatedProcessTime = processTimeRepository.save(processTime);
        return ProcessTimeMapper.mapToProcessTimeDto(updatedProcessTime);
    }

    @Override
    public ProcessTimeDto getProcessTimeById(Integer processTimeId) {
        ProcessTime processTime = processTimeRepository.findById(processTimeId)
                .orElseThrow(() -> new RuntimeException("ProcessTime is not found:" + processTimeId));
        return ProcessTimeMapper.mapToProcessTimeDto(processTime);
    }

    @Override
    public void deleteProcessTime(Integer processTimeId) {
        ProcessTime processTime = processTimeRepository.findById(processTimeId)
                .orElseThrow(() -> new RuntimeException("ProcessTime is not found:" + processTimeId));
        processTimeRepository.delete(processTime);
    }

    @Override
    public List<ProcessTimeDto> getAllProcessTime() {
        List<ProcessTime> processTimes = processTimeRepository.findAll();
        return processTimes.stream().map(ProcessTimeMapper::mapToProcessTimeDto).toList();
    }

}
