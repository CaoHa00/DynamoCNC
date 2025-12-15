package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.ProcessTimeDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.ProcessTimeMapper;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.LogRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.repository.ProcessTimeRepository;
import com.example.Dynamo_Backend.service.ProcessTimeService;
import com.example.Dynamo_Backend.util.DateTimeUtil;

@Service
public class ProcessTimeImplementation implements ProcessTimeService {
    @Autowired
    ProcessTimeRepository processTimeRepository;
    @Autowired
    DrawingCodeProcessRepository drawingCodeProcessRepository;
    @Autowired
    LogRepository logRepository;
    @Autowired
    MachineRepository machineRepository;

    @Override
    public ProcessTimeDto addProcessTime(ProcessTimeDto processTimeDto) {
        ProcessTime processTime = ProcessTimeMapper.mapToProcessTime(processTimeDto);
        ProcessTime savedProcessTime = processTimeRepository.save(processTime);
        return ProcessTimeMapper.mapToProcessTimeDto(savedProcessTime);
    }

    @Override
    public ProcessTimeDto updateProcessTime(Integer processTimeId, ProcessTimeDto processTimeDto) {
        ProcessTime processTime = processTimeRepository.findById(processTimeId)
                .orElseThrow(() -> new ResourceNotFoundException("ProcessTime is not found:" + processTimeId));
        DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository
                .findById(processTimeDto.getDrawingCodeProcessId())
                .orElseThrow(() -> new ResourceNotFoundException(
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
                .orElseThrow(() -> new ResourceNotFoundException("ProcessTime is not found:" + processTimeId));
        return ProcessTimeMapper.mapToProcessTimeDto(processTime);
    }

    @Override
    public void deleteProcessTime(Integer processTimeId) {
        ProcessTime processTime = processTimeRepository.findById(processTimeId)
                .orElseThrow(() -> new ResourceNotFoundException("ProcessTime is not found:" + processTimeId));
        processTimeRepository.delete(processTime);
    }

    @Override
    public List<ProcessTimeDto> getAllProcessTime() {
        List<ProcessTime> processTimes = processTimeRepository.findAll();
        return processTimes.stream().map(ProcessTimeMapper::mapToProcessTimeDto).toList();
    }

    @Override
    public ProcessTime calculateProcessTime(DrawingCodeProcess drawingCodeProcess) {
        List<Log> logs = logRepository.findByMachine_machineIdAndTimeStampBetweenOrderByTimeStampAsc(
                drawingCodeProcess.getMachine().getMachineId(),
                drawingCodeProcess.getStartTime(),
                drawingCodeProcess.getEndTime());
        ProcessTime processTime = new ProcessTime();

        if (!logs.isEmpty() && drawingCodeProcess.getMachine().getMachineId() <= 9) {
            float spanTime = 0L;
            float runTime = 0L;
            float pgTime = 0L;
            float stopTime = 0L;
            float offsetTime = 0L;

            List<Float> activeTime = machineRepository.calculateDurationsByStatusAndRange(
                    drawingCodeProcess.getMachine().getMachineId(), drawingCodeProcess.getStartTime(),
                    drawingCodeProcess.getEndTime());
            spanTime = (drawingCodeProcess.getEndTime() - drawingCodeProcess.getStartTime()) / 3600000f;
            pgTime = activeTime.get(3);
            offsetTime = activeTime.get(4);
            runTime = pgTime + offsetTime;
            stopTime = activeTime.get(5) + activeTime.get(6);

            // convert ms to hours
            processTime.setSpanTime(spanTime); // ms to hours
            processTime.setRunTime(runTime);
            processTime.setPgTime(pgTime);
            processTime.setStopTime(stopTime);
            processTime.setOffsetTime(offsetTime);
            processTime.setDrawingCodeProcess(drawingCodeProcess);
            return processTimeRepository.save(processTime);
        } else {
            float spanTime = (float) (drawingCodeProcess.getEndTime() - drawingCodeProcess.getStartTime())
                    / 3600000f;
            processTime.setSpanTime(spanTime);
            processTime.setRunTime(0f);
            processTime.setPgTime(0f);
            processTime.setStopTime(0f);
            processTime.setOffsetTime(0f);
            processTime.setDrawingCodeProcess(drawingCodeProcess);
            return processTimeRepository.save(processTime);
        }
        // processTimeSummaryService
        // .sumTimesByOrderDetailId(drawingCodeProcess.getOrderDetail().getOrderDetailId());
    }
}
