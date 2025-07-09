package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.Protocol;
import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.repository.ProtocolRepository;
import com.example.Dynamo_Backend.service.CurrentStatusService;
import com.example.Dynamo_Backend.service.LogService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CurrentStatusImplementation implements CurrentStatusService {
    private final CurrentStatusRepository currentStatusRepository;
    private final ProtocolRepository protocolRepository;
    private final @Lazy LogService logService;

    @Override
    public void addCurrentStatus(String payload) {
        String[] arr = payload.split("-");
        String machineId = arr[0];
        String protocolId = arr[1];
        CurrentStatus currentStatus = currentStatusRepository.findByMachineId(machineId);
        if (currentStatus == null) {
            currentStatus = new CurrentStatus();
        }
        Protocol operate = protocolRepository.findById(protocolId).orElse(null);
        Staff staff = operate.getStaff();
        currentStatus.setStaffId(staff.getId());

        DrawingCodeProcess process = operate.getProcess();
        currentStatus.setProcessId(process.getProcessId());

        currentStatus.setMachineId(arr[0]);
        currentStatus.setTime(arr[2]);
        currentStatus.setStatus(arr[3]);
        logService.addLog(currentStatus, process, staff);
        currentStatusRepository.save(currentStatus);
    }

    @Override
    public List<CurrentStatus> all() {
        List<CurrentStatus> all = currentStatusRepository.findAll();
        return all;
    }

}
