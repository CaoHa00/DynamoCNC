package com.example.Dynamo_Backend.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.Dynamo_Backend.dto.LogDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.mapper.LogMapper;
import com.example.Dynamo_Backend.repository.LogRepository;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.service.StaffService;
import com.example.Dynamo_Backend.service.LogService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LogImplementation implements LogService {
        StaffService staffService;
        LogRepository logRepository;
        OperateHistoryRepository operateHistoryRepository;

        @Override
        public void addLog(CurrentStatus currentStatus, Machine machine, Staff staff) {
                Log log = new Log();
                // log.setDrawingCodeProcess(process);
                log.setMachine(machine);
                log.setStaff(staff);
                log.setStatus(currentStatus.getStatus());
                log.setTimeStamp(System.currentTimeMillis());
                logRepository.save(log);

                OperateHistory operateHistory = operateHistoryRepository
                                .findInProgressByMachineId(machine.getMachineId()).stream().findFirst().orElse(null);
                if (operateHistory != null) {
                        List<Log> lastLog = logRepository.findTopByMachineIdAndTimeStampBeforeOrderByTimeStampDesc(
                                        machine.getMachineId(),
                                        log.getTimeStamp());
                        if (lastLog != null && lastLog.size() > 1 && lastLog.get(1).getStatus().equals("R1")) {
                                long timeDiff = log.getTimeStamp() - lastLog.get(1).getTimeStamp();
                                float hours = timeDiff / (1000f * 60f * 60f);
                                operateHistory.setPgTime(
                                                operateHistory.getPgTime() != null ? operateHistory.getPgTime() + hours
                                                                : hours);
                                operateHistoryRepository.save(operateHistory);
                        }
                }
        }

        @Override
        public LogDto getLogById(String statsId) {
                Log stats = logRepository.findById(statsId)
                                .orElseThrow(() -> new RuntimeException("stats is not found:" + statsId));
                return LogMapper.mapToStatsDto(stats);
        }

        @Override
        public void deleteLog(String statsId) {
                Log stats = logRepository.findById(statsId)
                                .orElseThrow(() -> new RuntimeException("stats is not found:" + statsId));
                logRepository.delete(stats);

        }

        @Override
        public List<LogDto> getAllLog() {
                List<Log> statstistics = logRepository.findAll();
                return statstistics.stream().map(LogMapper::mapToStatsDto).toList();
        }

}
