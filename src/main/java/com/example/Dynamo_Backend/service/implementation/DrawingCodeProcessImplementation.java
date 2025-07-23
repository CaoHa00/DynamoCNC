package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.dto.PlanDto;
import com.example.Dynamo_Backend.dto.RequestDto.DrawingCodeProcessResquestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.OperateHistory;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.mapper.DrawingCodeProcessMapper;
import com.example.Dynamo_Backend.mapper.MachineMapper;
import com.example.Dynamo_Backend.mapper.OrderDetailMapper;
import com.example.Dynamo_Backend.mapper.PlanMapper;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.repository.OperateHistoryRepository;
import com.example.Dynamo_Backend.repository.OrderDetailRepository;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.service.DrawingCodeProcessService;
import com.example.Dynamo_Backend.service.PlanService;

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

        // this api is for manager to add process(planned or not)
        @Override
        public DrawingCodeProcessDto addDrawingCodeProcess(DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                long createdTimestamp = System.currentTimeMillis();
                int status = 1;
                OrderDetail orderDetail = orderDetailRepository.findById(drawingCodeProcessDto.getOrderDetailId())
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode is not found:"
                                                                + drawingCodeProcessDto.getOrderDetailId()));

                DrawingCodeProcess drawingCodeProcess = DrawingCodeProcessMapper
                                .mapToDrawingCodeProcess(drawingCodeProcessDto);
                drawingCodeProcess.setOrderDetail(orderDetail);
                drawingCodeProcess.setCreatedDate(createdTimestamp);
                drawingCodeProcess.setUpdatedDate(createdTimestamp);
                drawingCodeProcess.setStatus(status);
                // drawingCodeProcess.setProcessStatus(1);
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
                        DrawingCodeProcessDto drawingCodeProcessDto) {
                MachineDto machine = null;
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));
                OrderDetail orderDetail = orderDetailRepository.findById(drawingCodeProcessDto.getOrderDetailId())
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode is not found:"
                                                                + drawingCodeProcessDto.getOrderDetailId()));
                OrderDetailDto updateOrderDetail = OrderDetailMapper.mapToOrderDetailDto(orderDetail);
                if (drawingCodeProcessDto.getMachineId() != null) {

                        Machine updateMachine = machineRepository.findById(drawingCodeProcessDto.getMachineId())
                                        .orElseThrow(
                                                        () -> new RuntimeException("DrawingCode Process is not found:"
                                                                        + drawingCodeProcessId));
                        drawingCodeProcess.setMachine(updateMachine);
                }
                long updatedTimestamp = System.currentTimeMillis();

                drawingCodeProcess.setOrderDetail(orderDetail);
                drawingCodeProcess.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                drawingCodeProcess.setOperateHistories(drawingCodeProcessDto.getStaffHistories());
                drawingCodeProcess.setLogs(drawingCodeProcess.getLogs());
                // drawingCodeProcess.setPgRunTime(drawingCodeProcessDto.getPgRunTime());
                // drawingCodeProcess.setOffsetRunTime(drawingCodeProcessDto.getOffsetRunTime());
                // drawingCodeProcess.setTotalRunningTime(drawingCodeProcessDto.getTotalRunningTime());
                // drawingCodeProcess.setTotalStopTime(drawingCodeProcessDto.getTotalStopTime());
                drawingCodeProcess.setPartNumber(drawingCodeProcessDto.getPartNumber());
                drawingCodeProcess.setStepNumber(drawingCodeProcessDto.getPartNumber());
                drawingCodeProcess.setStatus(drawingCodeProcessDto.getStatus());
                drawingCodeProcess.setUpdatedDate(updatedTimestamp);

                DrawingCodeProcess savedrawingCodeProcess = drawingCodeProcessRepository.save(drawingCodeProcess);
                return DrawingCodeProcessMapper.toDto(updateOrderDetail, machine, savedrawingCodeProcess);
        }

        @Override
        public DrawingCodeProcessDto getDrawingCodeProcessById(String drawingCodeProcessId) {
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));
                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(drawingCodeProcess);
        }

        @Override
        public DrawingCodeProcessDto getDrawingCodeProcessByMachineId(Integer machineId) {
                DrawingCodeProcess drawingCodeProcess;
                List<DrawingCodeProcess> processes = drawingCodeProcessRepository.findByMachine_MachineId(machineId);
                for (DrawingCodeProcess process : processes) {
                        if (process.getProcessStatus() != 2) {
                                processes.remove(process);
                        }
                }
                if (processes.size() > 1) {
                        new RuntimeException("Have more than 1 processes in progess!");
                }
                drawingCodeProcess = processes.get(0);

                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(drawingCodeProcess);
        }

        @Override
        public void deleteDrawingCodeProcess(String drawingCodeProcessId) {
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));
                drawingCodeProcessRepository.delete(drawingCodeProcess);
        }

        @Override
        public List<DrawingCodeProcessDto> getAllDrawingCodeProcess() {
                List<DrawingCodeProcess> drawingCodeProcesses = drawingCodeProcessRepository.findAll();
                List<DrawingCodeProcess> inProgressProcesses = new ArrayList<>();
                for (DrawingCodeProcess process : drawingCodeProcesses) {
                        if (process.getProcessStatus() == 2) {
                                inProgressProcesses.add(process);
                        }
                }
                return inProgressProcesses.stream().map(DrawingCodeProcessMapper::mapToDrawingCodeProcessDto).toList();
        }

        @Override
        public List<DrawingCodeProcessResponseDto> getAll() {
                List<DrawingCodeProcess> all = drawingCodeProcessRepository.findAll();
                List<DrawingCodeProcess> inProgressProcesses = new ArrayList<>();
                // for (DrawingCodeProcess process : all) {
                // if (process.getProcessStatus() == 2) {
                // inProgressProcesses.add(process);
                // }
                // }
                return all.stream().map(process -> {
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
                                                "DrawingCode is not found:" + drawingCodeProcessId));
                // Xử lý: chuyển trạng thái các process khác dùng máy thànhh off
                List<DrawingCodeProcess> processes = drawingCodeProcessRepository.findByMachine_MachineId(machineId);
                for (DrawingCodeProcess process2 : processes) {
                        if (process2.getProcessStatus() == 2) {
                                process2.setProcessStatus(3);
                        }
                }
                Machine machine = machineRepository.findById(machineId).orElseThrow(() -> new RuntimeException(
                                "Machine is not found:" + machineId));
                process.setMachine(machine);
                process.setProcessStatus(2);
                process.setStartTime(timestampNow);

                Staff staff = staffRepository.findById(staffId).orElseThrow(() -> new RuntimeException(
                                "Staff is not found:" + staffId));
                OperateHistory history = new OperateHistory();
                history.setStaff(staff);
                history.setStartTime(timestampNow);
                history.setStopTime((long) 0);
                history.setDrawingCodeProcess(process);
                history.setManufacturingPoint(process.getManufacturingPoint());

                operateHistoryRepository.save(history);

                // String sendMachine = "";
                // if (machineId < 10) {
                // sendMachine = "0" + machineId;
                // }
                // LocalDateTime now = LocalDateTime.now();
                // String formatted = now.format(DateTimeFormatter.ofPattern("MMddyyHH"));
                // String payload = sendMachine + "-" + formatted;
                // Message<String> message = MessageBuilder
                // .withPayload(payload)
                // .setHeader("mqtt_topic", "myTopic")
                // .build();
                // boolean sent = mqttOutboundChannel.send(message);
                // if (sent) {
                // System.out.println("Message sent successfully: " + payload);
                // } else {
                // System.err.println("Failed to send message: " + payload);
                // }
        }

}
