package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.ProcessTimeDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.mapper.ProcessTimeMapper;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.LogRepository;
import com.example.Dynamo_Backend.repository.ProcessTimeRepository;
import com.example.Dynamo_Backend.service.ProcessTimeService;

@Service
public class ProcessTimeImplementation implements ProcessTimeService {
    @Autowired
    ProcessTimeRepository processTimeRepository;
    @Autowired
    DrawingCodeProcessRepository drawingCodeProcessRepository;
    @Autowired
    LogRepository logRepository;

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

    @Override
    public ProcessTime calculateProcessTime(DrawingCodeProcess drawingCodeProcess) {
        List<Log> logs = logRepository.findByMachine_machineIdAndTimeStampBetweenOrderByTimeStampAsc(
                drawingCodeProcess.getMachine().getMachineId(),
                drawingCodeProcess.getStartTime(),
                drawingCodeProcess.getEndTime());
        ProcessTime processTime = new ProcessTime();
        Long doneTime = drawingCodeProcess.getEndTime() != null ? drawingCodeProcess.getEndTime()
                : System.currentTimeMillis();

        if (!logs.isEmpty() && drawingCodeProcess.getMachine().getMachineId() <= 9) {
            long spanTime = 0L;
            long runTime = 0L;
            long pgTime = 0L;
            long stopTime = 0L;
            long offsetTime = 0L;

            for (int i = 0; i < logs.size(); i++) {
                Log log = logs.get(i);
                String status = log.getStatus();

                if (i + 1 >= logs.size())
                    break;
                Log next = logs.get(i + 1);
                switch (status) {
                    case "R1":
                        pgTime += (next.getTimeStamp() - log.getTimeStamp());
                        runTime += (next.getTimeStamp() - log.getTimeStamp());
                        break;
                    case "R2":
                        offsetTime += (next.getTimeStamp() - log.getTimeStamp());
                        runTime += (next.getTimeStamp() - log.getTimeStamp());
                        break;
                    default:
                        stopTime += (next.getTimeStamp() - log.getTimeStamp());
                        break;
                }
            }
            // phòng trường hợp log đầu không phải R, tính theo giờ máy
            // for (int i = 0; i < logs.size() - 1; i++) {
            // Log log = logs.get(i);
            // if ("R1".equals(log.getStatus()) || "R2".equals(log.getStatus())) {
            // spanTime = logs.get(logs.size() - 1).getTimeStamp()
            // - logs.get(i).getTimeStamp();
            // break;
            // }
            // }

            spanTime = logs.get(logs.size() - 1).getTimeStamp()
                    - logs.get(0).getTimeStamp();

            // convert ms to hours
            processTime.setSpanTime(spanTime / 3600000f); // ms to hours
            processTime.setRunTime(runTime / 3600000f);
            processTime.setPgTime(pgTime / 3600000f);
            processTime.setStopTime(stopTime / 3600000f);
            processTime.setOffsetTime(offsetTime / 3600000f);
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
