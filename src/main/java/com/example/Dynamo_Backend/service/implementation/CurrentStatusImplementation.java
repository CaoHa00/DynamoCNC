package com.example.Dynamo_Backend.service.implementation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.config.MyWebSocketHandler;
import com.example.Dynamo_Backend.dto.CurrentStatusDto;
import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.dto.ResponseDto.CurrentStatusResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.ListCurrentStaffStatusDto;
import com.example.Dynamo_Backend.entities.CurrentStaff;
import com.example.Dynamo_Backend.entities.CurrentStatus;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.*;
import com.example.Dynamo_Backend.repository.CurrentStaffRepository;
import com.example.Dynamo_Backend.repository.CurrentStatusRepository;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.repository.StaffKpiRepository;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.service.CurrentStatusService;
import com.example.Dynamo_Backend.service.LogService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CurrentStatusImplementation implements CurrentStatusService {
    private final CurrentStatusRepository currentStatusRepository;
    private final CurrentStaffRepository currentStaffRepository;
    private final DrawingCodeProcessRepository drawingCodeProcessRepository;
    private final MachineRepository machineRepository;
    private final @Lazy LogService logService;

    private final StaffRepository staffRepository;

    private final StaffKpiRepository staffKpiRepository;

    @Override
    public void addCurrentStatus(String payload) {
        String[] arr = payload.split("-");
        String machineId = arr[0];
        int machineIdInt = Integer.parseInt(machineId) + 1;
        CurrentStatus currentStatus = currentStatusRepository.findByMachineId(machineIdInt);
        if (currentStatus == null) {
            currentStatus = new CurrentStatus();
        }
        CurrentStaff currentStaff = currentStaffRepository.findByMachine_MachineId(machineIdInt);
        if (currentStaff != null && currentStaff.getStaff() != null) {
            currentStatus.setStaffId(currentStaff.getStaff().getId());
        } else {
            currentStatus.setStaffId(null);
        }
        List<DrawingCodeProcess> drawingCodeProcesses = drawingCodeProcessRepository
                .findByMachine_MachineId(machineIdInt);
        if (drawingCodeProcesses.size() > 0) {
            for (DrawingCodeProcess drawingCodeProcess : drawingCodeProcesses) {
                if (drawingCodeProcess.getStartTime() != null && drawingCodeProcess.getEndTime() != null
                        && drawingCodeProcess.getStartTime() > drawingCodeProcess.getEndTime()) {
                    currentStatus.setProcessId(drawingCodeProcess.getProcessId());
                    break;
                }
            }
        } else {
            currentStatus.setProcessId(null);
        }

        currentStatus.setMachineId(machineIdInt);
        currentStatus.setStatus(arr[1]);

        if (arr.length < 3) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String nowStr = LocalDateTime.now().format(formatter);
            currentStatus.setTime(nowStr);
        } else {
            currentStatus.setTime(arr[2]);
        }
        Machine machine = machineRepository.findById(machineIdInt)
                .orElseThrow(() -> new BusinessException("Machine is not found when find machine for currentStatus"));
        logService.addLog(currentStatus, machine,
                currentStaff != null ? currentStaff.getStaff() : null);
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

    @Override
    public CurrentStatusDto addCurrentStatus(CurrentStatusDto currentStatusDto) {
        CurrentStatus currentStatus = new CurrentStatus();
        currentStatus.setMachineId(currentStatusDto.getMachineId());
        currentStatus.setStatus(currentStatusDto.getStatus());
        currentStatus.setTime(currentStatusDto.getTime());
        currentStatus.setMachineId(currentStatusDto.getMachineId());
        currentStatus = currentStatusRepository.save(currentStatus);
        return CurrentStatusMapper.mapToCurrentStatusDto(currentStatus);
    }

    @Override
    public CurrentStatusDto updateCurrentStatus(String id, CurrentStatusDto currentStatusDto) {
        CurrentStatus currentStatus = currentStatusRepository.findById(id)
                .orElseThrow(() -> new BusinessException("CurrentStatus not found with id: " + id));
        currentStatus.setMachineId(currentStatusDto.getMachineId());
        currentStatus.setStatus(currentStatusDto.getStatus());
        currentStatus.setTime(currentStatusDto.getTime());
        currentStatus = currentStatusRepository.save(currentStatus);
        return CurrentStatusMapper.mapToCurrentStatusDto(currentStatus);
    }

    @Override
    public void deleteCurrentStatus(String currentStatusId) {
        currentStatusRepository.deleteById(currentStatusId);
    }

    @Override
    public CurrentStatusDto getCurrentStatusById(String id) {
        CurrentStatus currentStatus = currentStatusRepository.findById(id)
                .orElseThrow(() -> new BusinessException("CurrentStatus not found with id: " + id));
        return CurrentStatusMapper.mapToCurrentStatusDto(currentStatus);
    }

    @Override
    public List<CurrentStatusDto> getAllCurrentStatus() {
        List<CurrentStatus> currentStatuses = currentStatusRepository.findAll();
        return currentStatuses.stream()
                .map(CurrentStatusMapper::mapToCurrentStatusDto)
                .toList();
    }

    @Override
    public List<CurrentStatusResponseDto> getCurrentStatusByGroupId(String groupId) {
        int currentMonth = LocalDate.now().getMonthValue(); // 1 = January, 12 = December
        int currentYear = LocalDate.now().getYear();

        List<Machine> machines = machineRepository.findMachinesByGroupIdLatestOrCurrent(groupId, currentMonth,
                currentYear);
        List<CurrentStatusResponseDto> result = new ArrayList<>();

        if (machines.isEmpty()) {
            return List.of(); // Return empty list
        }
        for (Machine machine : machines) {
            CurrentStatus currentStatus = currentStatusRepository.findByMachineId(machine.getMachineId());
            if (currentStatus == null) {
                continue;
            }
            CurrentStaff currentStaff = currentStaffRepository.findByMachine_MachineId(machine.getMachineId());
            if (currentStaff != null && currentStaff.getStaff() != null) {
                currentStaff.getStaff().setStaffKpis(null);
            }
            StaffDto staffDto = (currentStaff != null && currentStaff.getStaff() != null)
                    ? StaffMapper.mapStaffNameDto(currentStaff.getStaff())
                    : null;
            DrawingCodeProcess drawingCodeProcess = currentStatus.getProcessId() != null ? drawingCodeProcessRepository
                    .findById(currentStatus.getProcessId())
                    .orElse(null) : null;
            String drawingCodeName = drawingCodeProcess != null
                    ? drawingCodeProcess.getOrderDetail().getDrawingCode().getDrawingCodeName()
                    : null;
            Float pgTime = drawingCodeProcess != null
                    ? drawingCodeProcess.getPgTime()
                    : null;
            Long startTime = drawingCodeProcess != null
                    ? drawingCodeProcess.getStartTime()
                    : null;
            machine.setMachineKpis(null);
            CurrentStatusResponseDto responseDto = new CurrentStatusResponseDto(
                    currentStatus.getId(),
                    MachineMapper.mapOnlyMachineName(machine),
                    staffDto,
                    drawingCodeName,
                    pgTime,
                    startTime,
                    currentStatus.getTime(),
                    currentStatus.getStatus());
            result.add(responseDto);
        }
        return result;
    }

    @Override
    public List<ListCurrentStaffStatusDto> getCurrentStaffStatusByGroupId(String groupId) {
        int currentMonth = LocalDate.now().getMonthValue(); // 1 = January, 12 = December
        int currentYear = LocalDate.now().getYear();

        List<StaffKpi> staffKpis = staffKpiRepository.findByGroup_groupIdAndMonthAndYear(groupId, currentMonth,
                currentYear);

        List<ListCurrentStaffStatusDto> res = new ArrayList<>();

        if (staffKpis.isEmpty()) {
            return List.of(); // Return empty list
        }
        StaffDto staffDto = null;
        for (StaffKpi staffKpi : staffKpis) {
            List<CurrentStatus> currentStatuses = currentStatusRepository.findByStaffId(staffKpi.getStaff().getId());
            if (currentStatuses == null) {
                continue;
            }
            List<CurrentStatusResponseDto> result = new ArrayList<>();
            for (CurrentStatus currentStatus : currentStatuses) {
                Staff staff = staffRepository.findById(currentStatus.getStaffId()).orElse(null);
                staffDto = StaffMapper.mapStaffNameDto(staff);
                DrawingCodeProcess drawingCodeProcess = currentStatus.getProcessId() != null
                        ? drawingCodeProcessRepository
                                .findById(currentStatus.getProcessId())
                                .orElse(null)
                        : null;
                String drawingCodeName = drawingCodeProcess != null
                        ? drawingCodeProcess.getOrderDetail().getDrawingCode().getDrawingCodeName()
                        : null;
                Float pgTime = drawingCodeProcess != null
                        ? drawingCodeProcess.getPgTime()
                        : null;
                Long startTime = drawingCodeProcess != null
                        ? drawingCodeProcess.getStartTime()
                        : null;
                Machine machine = machineRepository.findById(currentStatus.getMachineId()).orElse(null);
                machine.setMachineKpis(null);
                CurrentStatusResponseDto responseDto = new CurrentStatusResponseDto(
                        currentStatus.getId(),
                        MachineMapper.mapOnlyMachineName(machine),
                        null,
                        drawingCodeName,
                        pgTime,
                        startTime,
                        currentStatus.getTime(),
                        currentStatus.getStatus());
                result.add(responseDto);
            }
            ListCurrentStaffStatusDto dto = new ListCurrentStaffStatusDto();
            dto.setStaffDto(staffDto);
            dto.setListStaffStatus(result);
            res.add(dto);
        }
        return res;
    }
}
