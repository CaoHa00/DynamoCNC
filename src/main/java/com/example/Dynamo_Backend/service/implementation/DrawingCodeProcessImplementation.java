package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.*;
import com.example.Dynamo_Backend.dto.RequestDto.DrawingCodeProcessResquestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;
import com.example.Dynamo_Backend.entities.*;
import com.example.Dynamo_Backend.mapper.*;
import com.example.Dynamo_Backend.repository.*;
import com.example.Dynamo_Backend.service.*;
import com.example.Dynamo_Backend.util.DateTimeUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DrawingCodeProcessImplementation implements DrawingCodeProcessService {
        OrderDetailRepository orderDetailRepository;
        MachineRepository machineRepository;
        DrawingCodeProcessRepository drawingCodeProcessRepository;
        private MessageChannel mqttOutboundChannel;
        StaffRepository staffRepository;
        PlanService planService;
        OperateHistoryRepository operateHistoryRepository;
        CurrentStaffService currentStaffService;

        // this api is for manager to add process(planned or not)
        @Override
        public DrawingCodeProcessDto addDrawingCodeProcess(DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                long createdTimestamp = System.currentTimeMillis();
                int status = 1;
                OrderDetail orderDetail = orderDetailRepository.findByOrderCode(drawingCodeProcessDto.getOrderCode())
                                .orElseThrow(() -> new RuntimeException(
                                                "OrderDetail is not found:"
                                                                + drawingCodeProcessDto.getOrderCode()));
                // Machine machine =
                // machineRepository.findById(drawingCodeProcessDto.getMachineId())
                // .orElseThrow(() -> new RuntimeException("Machine is not found:" +
                // drawingCodeProcessDto.getMachineId()));
                DrawingCodeProcess drawingCodeProcess = DrawingCodeProcessMapper
                                .mapToDrawingCodeProcess(drawingCodeProcessDto);
                drawingCodeProcess.setOrderDetail(orderDetail);
                // drawingCodeProcess.setMachine(machine);
                drawingCodeProcess.setCreatedDate(createdTimestamp);
                drawingCodeProcess.setUpdatedDate(createdTimestamp);
                drawingCodeProcess.setStatus(status);
                drawingCodeProcess.setProcessStatus(1);
                // check if it is added by manager or staff
                DrawingCodeProcess savedrawingCodeProcess = drawingCodeProcessRepository.save(drawingCodeProcess);
                if (drawingCodeProcessDto.getIsPlan() == 1) {
                        PlanDto plan = DrawingCodeProcessMapper.mapToPlanDto(drawingCodeProcessDto);
                        plan.setProcessId(savedrawingCodeProcess.getProcessId());
                        planService.addPlan(plan);
                        savedrawingCodeProcess.setPlans(new ArrayList<>());
                        savedrawingCodeProcess.getPlans().add(PlanMapper.mapToPlan(plan));
                }
                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(savedrawingCodeProcess);
        }

        @Override
        public DrawingCodeProcessResponseDto updateDrawingCodeProcess(String drawingCodeProcessId,
                        DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                MachineDto machine = null;
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));
                OrderDetail orderDetail = orderDetailRepository.findByOrderCode(drawingCodeProcessDto.getOrderCode())
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode is not found:"
                                                                + drawingCodeProcessDto.getOrderCode()));
                OrderDetailDto updateOrderDetail = OrderDetailMapper.mapToOrderDetailDto(orderDetail);
                if (drawingCodeProcessDto.getMachineId() != null) {
                        Machine updateMachine = machineRepository.findById(drawingCodeProcessDto.getMachineId())
                                        .orElseThrow(
                                                        () -> new RuntimeException("DrawingCode Process is not found:"
                                                                        + drawingCodeProcessId));
                        drawingCodeProcess.setMachine(updateMachine);
                }
                long updatedTimestamp = System.currentTimeMillis();
                Staff staff = staffRepository.findByStaffId(drawingCodeProcessDto.getStaffId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Staff is not found:" + drawingCodeProcessDto.getStaffId()));
                // cập nhật staff đang làm
                CurrentStaffDto currentStaffDto = new CurrentStaffDto(null, staff.getId(),
                                drawingCodeProcessDto.getMachineId(),
                                DateTimeUtil.convertTimestampToStringDate(updatedTimestamp));
                currentStaffService.addCurrentStaff(currentStaffDto);

                drawingCodeProcess.setOrderDetail(orderDetail);
                // drawingCodeProcess.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                drawingCodeProcess.setUpdatedDate(updatedTimestamp);

                DrawingCodeProcess savedrawingCodeProcess = drawingCodeProcessRepository.save(drawingCodeProcess);
                return DrawingCodeProcessMapper.toDto(updateOrderDetail, machine, savedrawingCodeProcess);
        }
        // @Override
        // public DrawingCodeProcessResponseDto updateDrawingCodeProcess(String
        // drawingCodeProcessId,
        // DrawingCodeProcessDto drawingCodeProcessDto) {
        // MachineDto machine = null;
        // DrawingCodeProcess drawingCodeProcess =
        // drawingCodeProcessRepository.findById(drawingCodeProcessId)
        // .orElseThrow(() -> new RuntimeException(
        // "DrawingCode Process is not found:" + drawingCodeProcessId));
        // OrderDetail orderDetail =
        // orderDetailRepository.findById(drawingCodeProcessDto.getOrderDetailId())
        // .orElseThrow(() -> new RuntimeException(
        // "DrawingCode is not found:"
        // + drawingCodeProcessDto.getOrderDetailId()));
        // OrderDetailDto updateOrderDetail =
        // OrderDetailMapper.mapToOrderDetailDto(orderDetail);
        // if (drawingCodeProcessDto.getMachineId() != null) {
        // Machine updateMachine =
        // machineRepository.findById(drawingCodeProcessDto.getMachineId())
        // .orElseThrow(
        // () -> new RuntimeException("DrawingCode Process is not found:"
        // + drawingCodeProcessId));
        // drawingCodeProcess.setMachine(updateMachine);
        // }
        // long updatedTimestamp = System.currentTimeMillis();

        // drawingCodeProcess.setOrderDetail(orderDetail);
        // drawingCodeProcess.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
        // drawingCodeProcess.setOperateHistories(drawingCodeProcessDto.getStaffHistories());
        // drawingCodeProcess.setLogs(drawingCodeProcess.getLogs());
        // drawingCodeProcess.setPartNumber(drawingCodeProcessDto.getPartNumber());
        // drawingCodeProcess.setStepNumber(drawingCodeProcessDto.getPartNumber());
        // drawingCodeProcess.setStatus(drawingCodeProcessDto.getStatus());
        // drawingCodeProcess.setUpdatedDate(updatedTimestamp);

        // DrawingCodeProcess savedrawingCodeProcess =
        // drawingCodeProcessRepository.save(drawingCodeProcess);
        // return DrawingCodeProcessMapper.toDto(updateOrderDetail, machine,
        // savedrawingCodeProcess);
        // }

        @Override
        public DrawingCodeProcessDto getDrawingCodeProcessById(String drawingCodeProcessId) {
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));
                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(drawingCodeProcess);
        }

        @Override
        public DrawingCodeProcessDto getDrawingCodeProcessByMachineId(Integer machineId) {
                DrawingCodeProcess drawingCodeProcess = new DrawingCodeProcess();
                List<DrawingCodeProcess> processes = drawingCodeProcessRepository.findByMachine_MachineId(machineId);
                List<DrawingCodeProcess> currentProcess = new ArrayList<>();
                for (DrawingCodeProcess process : processes) {
                        if (process.getProcessStatus() == 2) {
                                currentProcess.add(process);
                        }
                }
                if (currentProcess.size() > 1) {
                        new RuntimeException("Have more than 1 processes in progess!");
                } else if (currentProcess.size() == 1) {
                        drawingCodeProcess = currentProcess.get(0);
                } else {
                        return new DrawingCodeProcessDto();
                }

                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(drawingCodeProcess);
        }

        @Override
        public void deleteDrawingCodeProcess(String drawingCodeProcessId) {
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));
                drawingCodeProcessRepository.delete(drawingCodeProcess);
        }

        // get all to do process
        @Override
        public List<DrawingCodeProcessDto> getAllDrawingCodeProcess() {
                List<DrawingCodeProcess> drawingCodeProcesses = drawingCodeProcessRepository.findAll();
                List<DrawingCodeProcess> inProgressProcesses = new ArrayList<>();
                for (DrawingCodeProcess process : drawingCodeProcesses) {
                        if (process.getProcessStatus() == 1) {
                                inProgressProcesses.add(process);
                        }
                }
                return inProgressProcesses.stream().map(DrawingCodeProcessMapper::mapToDrawingCodeProcessDto).toList();
        }

        @Override
        public List<DrawingCodeProcessResponseDto> getAll() {
                List<DrawingCodeProcess> all = drawingCodeProcessRepository.findAll();
                List<DrawingCodeProcess> todoProcesses = new ArrayList<>();
                for (DrawingCodeProcess process : all) {
                        if (process.getProcessStatus() == 1) {
                                todoProcesses.add(process);
                        }
                }
                return todoProcesses.stream().map(process -> {
                        OrderDetailDto orderDetailDto = OrderDetailMapper.mapToOrderDetailDto(process.getOrderDetail());
                        Machine machine = process.getMachine();
                        MachineDto machineDto = (machine != null)
                                        ? MachineMapper.mapToMachineDto(machine)
                                        : null;
                        return DrawingCodeProcessMapper.toDto(orderDetailDto, machineDto, process);
                }).toList();
        }

        @Override
        public void receiveProcessFromTablet(String drawingCodeProcessId, Integer machineId, String staffId) {
                long timestampNow = System.currentTimeMillis();
                DrawingCodeProcess process = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCodeProcess is not found:" + drawingCodeProcessId));
                // Xử lý: chuyển trạng thái các process khác dùng máy thànhh off
                List<DrawingCodeProcess> processes = drawingCodeProcessRepository.findByMachine_MachineId(machineId);
                for (DrawingCodeProcess process2 : processes) {
                        if (process2.getProcessStatus() == 2) {
                                process2.setProcessStatus(3);
                        }
                }

                // check: just the empty machine(status=0) can start

                Machine machine = machineRepository.findById(machineId).orElseThrow(() -> new RuntimeException(
                                "Machine is not found:" + machineId));
                machine.setStatus(1);
                machineRepository.save(machine);
                process.setMachine(machine);
                process.setProcessStatus(2);
                process.setStartTime(timestampNow);
                process.setEndTime((long) 0);

                drawingCodeProcessRepository.save(process);

                Staff staff = staffRepository.findById(staffId).orElseThrow(() -> new RuntimeException(
                                "Staff is not found:" + staffId));
                // cập nhật staff đang làm
                CurrentStaffDto currentStaffDto = new CurrentStaffDto(null, staff.getId(), machine.getMachineId(),
                                DateTimeUtil.convertTimestampToStringDate(timestampNow));
                currentStaffService.addCurrentStaff(currentStaffDto);

                String sendMachine = "";
                if (machineId < 10) {
                        sendMachine = "0" + machineId;
                }
                LocalDateTime now = LocalDateTime.now();
                Integer productStatus;
                switch (process.getProcessType()) {
                        case "Main":
                                productStatus = 1;
                                break;
                        case "NG":
                                productStatus = 2;
                                break;
                        case "LK":
                                productStatus = 3;
                                break;
                        case "Electric":
                                productStatus = 4;
                                break;
                        default:
                                productStatus = 1;
                                break;
                }
                String formatted = now.format(DateTimeFormatter.ofPattern("MMddyyHH"));
                String payload = sendMachine + "*" + staff.getStaffId() + "*" + productStatus + "*"
                                + process.getOrderDetail().getOrderCode() + "*" + process.getPartNumber() + "*"
                                + process.getStepNumber() + "*" + process.getManufacturingPoint() + "*"
                                + process.getPgTime();
                Message<String> message = MessageBuilder
                                .withPayload(payload)
                                .setHeader("mqtt_topic", "myTopic")
                                .build();
                boolean sent = mqttOutboundChannel.send(message);
                if (sent) {
                        System.out.println("Message sent successfully: " + payload);
                } else {
                        System.err.println("Failed to send message: " + payload);
                }
        }

        @Override
        public DrawingCodeProcessDto addProcessByOperator(DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                long createdTimestamp = System.currentTimeMillis();
                int status = 1;
                OrderDetail orderDetail = orderDetailRepository.findByOrderCode(drawingCodeProcessDto.getOrderCode())
                                .orElseThrow(() -> new RuntimeException(
                                                "OrderDetail is not found:"
                                                                + drawingCodeProcessDto.getOrderCode()));
                Machine machine = machineRepository.findById(drawingCodeProcessDto.getMachineId())
                                .orElseThrow(() -> new RuntimeException("Machine is not found:" +
                                                drawingCodeProcessDto.getMachineId()));
                DrawingCodeProcess drawingCodeProcess = new DrawingCodeProcess();
                drawingCodeProcess.setOrderDetail(orderDetail);
                drawingCodeProcess.setMachine(machine);
                drawingCodeProcess.setCreatedDate(createdTimestamp);
                drawingCodeProcess.setUpdatedDate(createdTimestamp);
                drawingCodeProcess.setStartTime(createdTimestamp);
                drawingCodeProcess.setEndTime((long) 0);
                drawingCodeProcess.setStatus(status);
                drawingCodeProcess.setProcessStatus(2);
                drawingCodeProcess.setProcessType(drawingCodeProcessDto.getProcessType());
                drawingCodeProcess.setIsPlan(0);
                drawingCodeProcess.setPartNumber(drawingCodeProcessDto.getPartNumber());
                drawingCodeProcess.setStepNumber(drawingCodeProcessDto.getStepNumber());
                drawingCodeProcess.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                drawingCodeProcess.setPgTime(drawingCodeProcessDto.getPgTime());
                DrawingCodeProcess savedrawingCodeProcess = drawingCodeProcessRepository.save(drawingCodeProcess);
                Staff staff = staffRepository.findByStaffId(drawingCodeProcessDto.getStaffId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Staff is not found:" + drawingCodeProcessDto.getStaffId()));
                // cập nhật staff đang làm
                CurrentStaffDto currentStaffDto = new CurrentStaffDto(null, staff.getId(), machine.getMachineId(),
                                DateTimeUtil.convertTimestampToStringDate(createdTimestamp));
                currentStaffService.addCurrentStaff(currentStaffDto);

                machine.setStatus(1);
                machineRepository.save(machine);

                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(savedrawingCodeProcess);
        }

        @Override
        public void doneProcess(String processId) {
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository
                                .findById(processId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCodeProcess is not found:" + processId));
                Long doneTime = System.currentTimeMillis();
                OperateHistory operateHistory = operateHistoryRepository
                                .findByDrawingCodeProcess_processId(drawingCodeProcess.getProcessId())
                                .stream()
                                .filter(operate -> operate.getInProgress() == 1)
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException(
                                                "No in-progress operate history found for process ID: "
                                                                + drawingCodeProcess.getProcessId()));
                drawingCodeProcess.setProcessStatus(3);
                drawingCodeProcess.setEndTime(doneTime);
                drawingCodeProcess.setUpdatedDate(doneTime);
                drawingCodeProcessRepository.save(drawingCodeProcess);

                operateHistory.setStopTime(doneTime);
                operateHistory.setInProgress(0);
                operateHistoryRepository.save(operateHistory);

                Machine machine = machineRepository.findById(drawingCodeProcess.getMachine().getMachineId())
                                .orElseThrow(() -> new RuntimeException("Machine is not found:" +
                                                drawingCodeProcess.getMachine().getMachineId()));
                machine.setStatus(0);
                machineRepository.save(machine);

                CurrentStaffDto currentStaffDto = currentStaffService
                                .getCurrentStaffByMachineId(machine.getMachineId());
                currentStaffService.deleteCurrentStaff(currentStaffDto.getId());
        }
}
