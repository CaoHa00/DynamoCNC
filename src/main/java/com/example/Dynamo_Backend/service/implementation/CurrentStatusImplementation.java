package com.example.Dynamo_Backend.service.implementation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.config.MyWebSocketHandler;
import com.example.Dynamo_Backend.entities.CurrentStaff;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.mapper.CurrentStatusMapper;
import com.example.Dynamo_Backend.repository.CurrentStaffRepository;
import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.service.CurrentStatusService;
import com.example.Dynamo_Backend.service.LogService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CurrentStatusImplementation implements CurrentStatusService {
    private final CurrentStatusRepository currentStatusRepository;
    private final CurrentStaffRepository currentStaffRepository;
    private final DrawingCodeProcessRepository drawingCodeProcessRepository;
    private final @Lazy LogService logService;

    @Override
    public void addCurrentStatus(String payload) {
        String[] arr = payload.split("-");
        String machineId = arr[0];
        CurrentStatus currentStatus = currentStatusRepository.findByMachineId(machineId);
        if (currentStatus == null) {
            currentStatus = new CurrentStatus();
        }
        CurrentStaff currentStaff = currentStaffRepository.findByMachine_MachineId(Integer.parseInt(machineId));
        if (currentStaff != null) {
            currentStatus.setStaffId(currentStaff.getStaff().getId());
        } else {
            currentStatus.setStaffId(null);
        }
        List<DrawingCodeProcess> drawingCodeProcesses = drawingCodeProcessRepository
                .findByMachine_MachineId(Integer.parseInt(machineId));
        if (drawingCodeProcesses.size() > 0) {
            for (DrawingCodeProcess drawingCodeProcess : drawingCodeProcesses) {
                if (drawingCodeProcess.getStartTime() > drawingCodeProcess.getEndTime()) {
                    currentStatus.setProcessId(drawingCodeProcess.getProcessId());
                    break;
                }
            }
        } else {
            currentStatus.setProcessId(null);
        }

        currentStatus.setMachineId(arr[0]);
        currentStatus.setStatus(arr[1]);

        if (arr.length < 3) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String nowStr = LocalDateTime.now().format(formatter);
            currentStatus.setTime(nowStr);
        } else {
            currentStatus.setTime(arr[2]);
        }
        DrawingCodeProcess process = drawingCodeProcessRepository
                .findById(currentStatus.getProcessId())
                .orElseThrow(() -> new RuntimeException("Process is not found when find process for currentStatus"));

        logService.addLog(currentStatus, process, currentStaff.getStaff());
        currentStatusRepository.save(currentStatus);

        List<CurrentStatus> currentStatuses = currentStatusRepository.findAll();
        try {
            MyWebSocketHandler.sendMachineStatusToClients(currentStatuses.stream()
                    .map(CurrentStatusMapper::mapToCurrentStatusDto).toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CurrentStatus> all() {
        List<CurrentStatus> all = currentStatusRepository.findAll();
        return all;
    }

}
