package com.example.Dynamo_Backend.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.Dynamo_Backend.dto.LogDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.mapper.LogMapper;
import com.example.Dynamo_Backend.repository.LogRepository;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.service.StaffService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.service.LogService;
import com.example.Dynamo_Backend.service.MachineSegmentServiceImplementation;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LogImplementation implements LogService {
        StaffService staffService;
        LogRepository logRepository;
        OperateHistoryRepository operateHistoryRepository;
        MachineSegmentServiceImplementation machineSegmentService;

        @Override
        public void addLog(CurrentStatus currentStatus, Machine machine, Staff staff, Long timeStamp) {
                Log log = new Log();
                log.setMachine(machine);
                log.setStaff(staff);
                log.setStatus(currentStatus.getStatus());
                log.setTimeStamp(timeStamp);
                logRepository.save(log);

                // OperateHistory operateHistory = operateHistoryRepository
                // .findInProgressByMachineId(machine.getMachineId()).stream().findFirst().orElse(null);
                // if (operateHistory != null) {
                // Log lastLog = logRepository
                // .findTopByMachineIdAndTimeStampBeforeOrderByTimeStampDesc(
                // machine.getMachineId(),
                // log.getTimeStamp())
                // .stream().findFirst().orElse(null);
                // if (lastLog != null && "R1".equals(lastLog.getStatus())) {
                // long timeDiff = log.getTimeStamp() - lastLog.getTimeStamp();
                // Integer minutes = (int) timeDiff / (1000 * 60);
                // operateHistory.setPgTime(
                // operateHistory.getPgTime() != null
                // ? operateHistory.getPgTime() + minutes
                // : minutes);
                // operateHistoryRepository.save(operateHistory);
                // }
                // }
        }

        @Override
        public LogDto getLogById(String statsId) {
                Log stats = logRepository.findById(statsId)
                                .orElseThrow(() -> new ResourceNotFoundException("stats is not found:" + statsId));
                return LogMapper.mapToStatsDto(stats);
        }

        @Override
        public void deleteLog(String statsId) {
                Log stats = logRepository.findById(statsId)
                                .orElseThrow(() -> new ResourceNotFoundException("stats is not found:" + statsId));
                logRepository.delete(stats);

        }

        @Override
        public List<LogDto> getAllLog() {
                List<Log> statstistics = logRepository.findAll();
                return statstistics.stream().map(LogMapper::mapToStatsDto).toList();
        }

        @Override
        public void reSegment() {
                for (int i = 1; i <= 9; i++) {
                        List<Log> logs = logRepository.findByMachine_machineIdOrderByTimeStampAsc(i);
                        for (int j = 0; j < logs.size(); j++) {
                                machineSegmentService.handleNewEvent(i, logs.get(j).getStatus(),
                                                DateTimeUtil.convertLongToLocalDateTime(logs.get(j).getTimeStamp()),
                                                null, null);
                        }

                }
        }

}
