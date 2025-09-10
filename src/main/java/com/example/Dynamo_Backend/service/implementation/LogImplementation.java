package com.example.Dynamo_Backend.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.Dynamo_Backend.dto.LogDto;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.mapper.LogMapper;
import com.example.Dynamo_Backend.repository.LogRepository;
import com.example.Dynamo_Backend.service.StaffService;
import com.example.Dynamo_Backend.service.LogService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LogImplementation implements LogService {
        StaffService staffService;
        LogRepository logRepository;

        @Override
        public void addLog(CurrentStatus currentStatus, Machine machine, Staff staff) {
                Log log = new Log();
                // log.setDrawingCodeProcess(process);
                log.setMachine(machine);
                log.setStaff(staff);
                log.setStatus(currentStatus.getStatus());
                log.setTimeStamp(System.currentTimeMillis());
                logRepository.save(log);
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
