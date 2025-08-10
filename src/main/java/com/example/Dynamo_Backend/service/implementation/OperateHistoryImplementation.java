package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.OperateHistoryDto;
import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.entities.CurrentStaff;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.mapper.DrawingCodeProcessMapper;

import com.example.Dynamo_Backend.mapper.OperateHistoryMapper;
import com.example.Dynamo_Backend.mapper.StaffMapper;
import com.example.Dynamo_Backend.repository.CurrentStaffRepository;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.service.DrawingCodeProcessService;
import com.example.Dynamo_Backend.service.OperateHistoryService;
import com.example.Dynamo_Backend.service.StaffService;
import com.example.Dynamo_Backend.util.DateTimeUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OperateHistoryImplementation implements OperateHistoryService {
        DrawingCodeProcessService drawingCodeProcessService;
        StaffService staffService;
        OperateHistoryRepository operateHistoryRepository;
        CurrentStaffRepository currentStaffRepository;

        @Override
        public OperateHistoryDto addOperateHistory(String payload) {
                String[] arr = payload.split("-");
                String machineId = arr[0];
                String status = arr[1];
                DrawingCodeProcessDto drawingCodeProcessDto = drawingCodeProcessService
                                .getProcessDtoByMachineId(Integer.parseInt(machineId));
                OperateHistory operateHistory = null;
                long currentTimestamp = System.currentTimeMillis();
                CurrentStaff currentStaff = currentStaffRepository
                                .findByMachine_MachineId(Integer.parseInt(machineId));
                if (currentStaff != null) {
                        // nếu máy chạy thì cập nhật
                        if (status.contains("R")) {
                                List<OperateHistory> operateHistories = operateHistoryRepository
                                                .findByDrawingCodeProcess_processId(
                                                                drawingCodeProcessDto.getProcessId());
                                // nếu process chưa đc vận hành
                                for (OperateHistory operate : operateHistories) {
                                        if (operate.getInProgress() == 1) {
                                                operateHistory = operate;
                                        }
                                }
                                if (operateHistories.size() == 0 || operateHistory == null) {
                                        operateHistory = new OperateHistory();
                                        operateHistory.setStaff(currentStaff.getStaff());
                                        operateHistory.setManufacturingPoint(
                                                        drawingCodeProcessDto.getManufacturingPoint());
                                        operateHistory.setDrawingCodeProcess(
                                                        DrawingCodeProcessMapper
                                                                        .mapToDrawingCodeProcess(
                                                                                        drawingCodeProcessDto));
                                        operateHistory.setStartTime(currentTimestamp);
                                        operateHistory.setStopTime((long) 0);
                                        operateHistory.setInProgress(1);
                                        OperateHistory saveOperateHistory = operateHistoryRepository
                                                        .save(operateHistory);
                                        return OperateHistoryMapper.mapToOperateHistoryDto(saveOperateHistory);
                                } else {
                                        if (!operateHistory.getStaff().getId()
                                                        .equals(currentStaff.getStaff().getId())) {
                                                operateHistory.setStopTime(currentTimestamp);
                                                operateHistory.setInProgress(0);
                                                operateHistoryRepository.save(operateHistory);
                                                operateHistory.setManufacturingPoint(
                                                                drawingCodeProcessDto.getManufacturingPoint());
                                                operateHistory = new OperateHistory();
                                                operateHistory.setDrawingCodeProcess(DrawingCodeProcessMapper
                                                                .mapToDrawingCodeProcess(drawingCodeProcessDto));
                                                operateHistory.setStaff(currentStaff.getStaff());
                                                operateHistory.setStartTime(currentTimestamp);
                                                operateHistory.setStopTime((long) 0);
                                                operateHistory.setInProgress(0);
                                                OperateHistory saveOperateHistory = operateHistoryRepository
                                                                .save(operateHistory);
                                                return OperateHistoryMapper.mapToOperateHistoryDto(saveOperateHistory);
                                        }
                                }
                        }
                }
                return new OperateHistoryDto(null, 0, null, null, 0, null, null);
        }

        @Override
        public OperateHistoryDto addOperateHistory(OperateHistoryDto operateHistoryDto) {
                DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService
                                .getDrawingCodeProcessById(operateHistoryDto.getDrawingCodeProcessId());
                DrawingCodeProcess newdrawingCodeProcess = DrawingCodeProcessMapper
                                .mapToDrawingCodeProcess(drawingCodeProcess);
                StaffDto staff = staffService.getStaffById(operateHistoryDto.getStaffId());
                Staff newStaff = StaffMapper.mapToStaff(staff);

                OperateHistory operateHistory = OperateHistoryMapper
                                .mapToOperateHistory(operateHistoryDto);
                operateHistory.setStaff(newStaff);
                operateHistory.setDrawingCodeProcess(newdrawingCodeProcess);

                OperateHistory saveOperateHistory = operateHistoryRepository.save(operateHistory);
                return OperateHistoryMapper.mapToOperateHistoryDto(saveOperateHistory);
        }

        @Override
        public OperateHistoryDto updateOperateHistory(String Id, OperateHistoryDto operateHistoryDto) {
                OperateHistory operateHistory = operateHistoryRepository.findById(Id)
                                .orElseThrow(() -> new RuntimeException("DrawingCode is not found:" + Id));
                DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService
                                .getDrawingCodeProcessById(operateHistoryDto.getDrawingCodeProcessId());
                DrawingCodeProcess updateDrawingCodeProcess = DrawingCodeProcessMapper
                                .mapToDrawingCodeProcess(drawingCodeProcess);
                StaffDto staff = staffService.getStaffById(operateHistoryDto.getOperateHistoryId());
                Staff updateStaff = StaffMapper.mapToStaff(staff);

                operateHistory.setStaff(updateStaff);
                operateHistory.setManufacturingPoint(operateHistoryDto.getManufacturingPoint());
                operateHistory.setStartTime(DateTimeUtil.convertStringToTimestamp(operateHistoryDto.getStartTime()));
                operateHistory.setStopTime(DateTimeUtil.convertStringToTimestamp(operateHistoryDto.getStopTime()));
                operateHistory.setDrawingCodeProcess(updateDrawingCodeProcess);

                OperateHistory updateOperateHistory = operateHistoryRepository.save(operateHistory);
                return OperateHistoryMapper.mapToOperateHistoryDto(updateOperateHistory);
        }

        @Override
        public OperateHistoryDto getOperateHistoryById(String Id) {
                OperateHistory operateHistory = operateHistoryRepository.findById(Id)
                                .orElseThrow(() -> new RuntimeException("OperateHistory is not found:" + Id));
                return OperateHistoryMapper.mapToOperateHistoryDto(operateHistory);
        }

        @Override
        public void deleteOperateHistory(String Id) {
                OperateHistory operateHistory = operateHistoryRepository.findById(Id)
                                .orElseThrow(() -> new RuntimeException("DrawingCode is not found:" + Id));
                operateHistoryRepository.delete(operateHistory);
        }

        @Override
        public List<OperateHistoryDto> getAllOperateHistory() {
                List<OperateHistory> operateHistories = operateHistoryRepository.findAll();
                return operateHistories.stream().map(OperateHistoryMapper::mapToOperateHistoryDto).toList();
        }

}
