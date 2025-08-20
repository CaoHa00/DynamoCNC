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

    @Override
    public ProcessTime calculateProcessTime(DrawingCodeProcess drawingCodeProcess) {
        List<Log> logs = drawingCodeProcess.getLogs();
        ProcessTime processTime = new ProcessTime();
        Long doneTime = System.currentTimeMillis();
        logs.sort((log1, log2) -> Long.compare(log1.getTimeStamp(), log2.getTimeStamp()));
        if (!logs.isEmpty() && drawingCodeProcess.getMachine().getMachineId() <= 9) {
            long spanTime = 0L;
            long runTime = 0L;
            long pgTime = 0L;
            long stopTime = 0L;
            long offsetTime = 0L;

            Long lastStart = null;
            String lastStatus = null;

            for (int i = 0; i < logs.size(); i++) {
                Log log = logs.get(i);
                String status = log.getStatus();
                Long time = log.getTimeStamp();

                if ("R1".equals(status) || "R2".equals(status)) {
                    lastStart = time;
                    lastStatus = status;
                } else if (("S1".equals(status) || "S2".equals(status)) && lastStart != null) {
                    long duration = time - lastStart;
                    runTime += duration;
                    if ("R1".equals(lastStatus))
                        pgTime += duration;
                    if ("R2".equals(lastStatus))
                        offsetTime += duration;
                    lastStart = null;
                    lastStatus = null;
                }
                if (("S1".equals(status) || "S2".equals(status)) && i + 1 < logs.size()) {
                    Log nextLog = logs.get(i + 1);
                    if ("R1".equals(nextLog.getStatus()) || "R2".equals(nextLog.getStatus())) {
                        stopTime += nextLog.getTimeStamp() - time;
                    }
                }
            }
            // phòng trường hợp log đầu không phải R, tính theo giờ máy
            for (int i = 0; i < logs.size() - 1; i++) {
                Log log = logs.get(i);
                if ("R1".equals(log.getStatus()) || "R2".equals(log.getStatus())) {
                    spanTime = logs.get(logs.size() - 1).getTimeStamp()
                            - logs.get(i).getTimeStamp();
                    break;
                }
            }

            int lastIndex = logs.size() - 1;
            if (!"S1".equals(logs.get(lastIndex).getStatus())
                    || !"S2".equals(logs.get(lastIndex).getStatus())) {
                runTime += doneTime - logs.get(lastIndex).getTimeStamp();
            }

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
