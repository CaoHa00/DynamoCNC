package com.example.Dynamo_Backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.OperateHistoryDto;
import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.entities.CurrentStaff;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.ShiftInfo;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.TempProcess;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.DrawingCodeProcessMapper;

import com.example.Dynamo_Backend.mapper.OperateHistoryMapper;
import com.example.Dynamo_Backend.mapper.StaffMapper;
import com.example.Dynamo_Backend.repository.CurrentStaffRepository;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.repository.TempProcessRepository;
import com.example.Dynamo_Backend.service.DrawingCodeProcessService;
import com.example.Dynamo_Backend.service.OperateHistoryService;
import com.example.Dynamo_Backend.service.StaffService;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.example.Dynamo_Backend.util.ShiftUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OperateHistoryImplementation implements OperateHistoryService {
        DrawingCodeProcessRepository drawingCodeProcessRepository;
        StaffService staffService;
        OperateHistoryRepository operateHistoryRepository;
        CurrentStaffRepository currentStaffRepository;
        TempProcessRepository tempProcessRepository;

        @Override
        public void addOperateHistory(String payload) {
                if (!payload.contains("*")) {
                        String[] arr = payload.split("-");
                        String machineId = arr[0];
                        String status = arr[1];
                        if (status.contains("R")) {

                                int machineIdInt = Integer.parseInt(machineId) + 1;

                                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository
                                                .findByMachine_MachineIdAndProcessStatus(machineIdInt, 2);
                                handleOperate(machineIdInt, drawingCodeProcess);

                        }
                }

        }

        @Override
        public void handleOperate(Integer machineId, DrawingCodeProcess drawingCodeProcess) {
                DrawingCodeProcessDto drawingCodeProcessDto = null;
                if (drawingCodeProcess != null) {

                        drawingCodeProcessDto = DrawingCodeProcessMapper
                                        .mapToDrawingCodeProcessDto(drawingCodeProcess);

                        OperateHistory operateHistory = null;

                        long currentTimestamp = System.currentTimeMillis();
                        CurrentStaff currentStaff = currentStaffRepository
                                        .findByMachine_MachineId(machineId);
                        if (currentStaff != null) {
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
                                        ShiftInfo shift = ShiftUtil.resolveShift(DateTimeUtil
                                                        .convertLongToLocalDateTime(currentTimestamp));
                                        operateHistory.setLogDate(shift.getLogDate());
                                        operateHistory.setStopTime((long) 0);
                                        operateHistory.setPgTime(0);
                                        operateHistory.setInProgress(1);
                                        operateHistory.setPgTime(drawingCodeProcessDto.getPgTime());
                                        operateHistoryRepository
                                                        .save(operateHistory);

                                } else {
                                        if (!operateHistory.getStaff().getId()
                                                        .equals(currentStaff.getStaff().getId())) {

                                                TempProcess tempProcess = tempProcessRepository
                                                                .findByProcessId(drawingCodeProcessDto
                                                                                .getProcessId());
                                                Integer pgTime = operateHistory.getPgTime()
                                                                - tempProcess.getPgTime();
                                                Integer point = operateHistory.getManufacturingPoint()
                                                                - tempProcess.getPoint();
                                                operateHistory.setPgTime(pgTime);
                                                operateHistory.setManufacturingPoint(point);
                                                operateHistory.setStopTime(currentTimestamp);
                                                operateHistory.setInProgress(0);

                                                operateHistoryRepository.save(operateHistory);

                                                operateHistory = new OperateHistory();
                                                ShiftInfo shift = ShiftUtil.resolveShift(
                                                                DateTimeUtil.convertLongToLocalDateTime(
                                                                                currentTimestamp));
                                                operateHistory.setLogDate(shift.getLogDate());
                                                operateHistory.setDrawingCodeProcess(
                                                                DrawingCodeProcessMapper
                                                                                .mapToDrawingCodeProcess(
                                                                                                drawingCodeProcessDto));
                                                operateHistory.setStaff(currentStaff.getStaff());
                                                operateHistory.setManufacturingPoint(
                                                                tempProcess.getPoint());

                                                operateHistory.setManufacturingPoint(
                                                                tempProcess.getPoint());
                                                operateHistory.setPgTime(tempProcess.getPgTime());
                                                operateHistory.setStartTime(currentTimestamp);
                                                operateHistory.setStopTime((long) 0);
                                                operateHistory.setInProgress(1);
                                                operateHistoryRepository
                                                                .save(operateHistory);

                                        }
                                }

                        }

                }
        }

        // @Override
        // public OperateHistoryDto addOperateHistory(OperateHistoryDto
        // operateHistoryDto) {
        // DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService
        // .getDrawingCodeProcessById(operateHistoryDto.getDrawingCodeProcessId());
        // DrawingCodeProcess newdrawingCodeProcess = DrawingCodeProcessMapper
        // .mapToDrawingCodeProcess(drawingCodeProcess);
        // StaffDto staff = staffService.getStaffById(operateHistoryDto.getStaffId());
        // Staff newStaff = StaffMapper.mapToStaff(staff);

        // OperateHistory operateHistory = OperateHistoryMapper
        // .mapToOperateHistory(operateHistoryDto);
        // operateHistory.setStaff(newStaff);
        // operateHistory.setDrawingCodeProcess(newdrawingCodeProcess);

        // OperateHistory saveOperateHistory =
        // operateHistoryRepository.save(operateHistory);
        // return OperateHistoryMapper.mapToOperateHistoryDto(saveOperateHistory);
        // }

        // @Override
        // public OperateHistoryDto updateOperateHistory(String Id, OperateHistoryDto
        // operateHistoryDto) {
        // OperateHistory operateHistory = operateHistoryRepository.findById(Id)
        // .orElseThrow(() -> new ResourceNotFoundException("DrawingCode is not found:"
        // + Id));
        // DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService
        // .getDrawingCodeProcessById(operateHistoryDto.getDrawingCodeProcessId());
        // DrawingCodeProcess updateDrawingCodeProcess = DrawingCodeProcessMapper
        // .mapToDrawingCodeProcess(drawingCodeProcess);
        // StaffDto staff =
        // staffService.getStaffById(operateHistoryDto.getOperateHistoryId());
        // Staff updateStaff = StaffMapper.mapToStaff(staff);

        // operateHistory.setStaff(updateStaff);
        // operateHistory.setManufacturingPoint(operateHistoryDto.getManufacturingPoint());
        // operateHistory.setStartTime(DateTimeUtil.convertStringToTimestamp(operateHistoryDto.getStartTime()));
        // operateHistory.setStopTime(DateTimeUtil.convertStringToTimestamp(operateHistoryDto.getStopTime()));
        // operateHistory.setDrawingCodeProcess(updateDrawingCodeProcess);

        // OperateHistory updateOperateHistory =
        // operateHistoryRepository.save(operateHistory);
        // return OperateHistoryMapper.mapToOperateHistoryDto(updateOperateHistory);
        // }

        @Override
        public OperateHistoryDto getOperateHistoryById(String Id) {
                OperateHistory operateHistory = operateHistoryRepository.findById(Id)
                                .orElseThrow(() -> new ResourceNotFoundException("OperateHistory is not found:" + Id));
                return OperateHistoryMapper.mapToOperateHistoryDto(operateHistory);
        }

        @Override
        public void deleteOperateHistory(String Id) {
                OperateHistory operateHistory = operateHistoryRepository.findById(Id)
                                .orElseThrow(() -> new ResourceNotFoundException("DrawingCode is not found:" + Id));
                operateHistoryRepository.delete(operateHistory);
        }

        @Override
        public List<OperateHistoryDto> getAllOperateHistory() {
                List<OperateHistory> operateHistories = operateHistoryRepository.findAll();
                return operateHistories.stream().map(OperateHistoryMapper::mapToOperateHistoryDto).toList();
        }

}
