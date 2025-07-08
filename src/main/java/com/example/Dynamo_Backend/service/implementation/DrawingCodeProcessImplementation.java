package com.example.Dynamo_Backend.service.implementation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.config.IdGenerator;
import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;
import com.example.Dynamo_Backend.entities.DrawingCode;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.Protocol;
import com.example.Dynamo_Backend.mapper.DrawingCodeMapper;
import com.example.Dynamo_Backend.mapper.DrawingCodeProcessMapper;
import com.example.Dynamo_Backend.mapper.MachineMapper;
import com.example.Dynamo_Backend.mapper.OrderDetailMapper;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.DrawingCodeRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.repository.OrderDetailRepository;
import com.example.Dynamo_Backend.repository.StaffRepository;
import com.example.Dynamo_Backend.repository.ProtocolRepository;
import com.example.Dynamo_Backend.service.DrawingCodeProcessService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DrawingCodeProcessImplementation implements DrawingCodeProcessService {
        OrderDetailRepository orderDetailRepository;
        ProtocolRepository protocolRepository;
        MachineRepository machineRepository;
        DrawingCodeProcessRepository drawingCodeProcessRepository;
        private MessageChannel mqttOutboundChannel;
        StaffRepository staffRepository;

        @Override
        public DrawingCodeProcessDto addDrawingCodeProcess(DrawingCodeProcessDto drawingCodeProcessDto) {
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
                drawingCodeProcess.setProcessStatus(1);
                drawingCodeProcess.setQcNote("null");

                DrawingCodeProcess savedrawingCodeProcess = drawingCodeProcessRepository.save(drawingCodeProcess);
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
                drawingCodeProcess.setPgTime(drawingCodeProcessDto.getPgTime());
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
                        if (process.getProcessStatus() != 1) {
                                inProgressProcesses.add(process);
                        }
                }
                return inProgressProcesses.stream().map(DrawingCodeProcessMapper::mapToDrawingCodeProcessDto).toList();
        }

        @Override
        public List<DrawingCodeProcessResponseDto> getAll() {
                List<DrawingCodeProcess> all = drawingCodeProcessRepository.findAll();
                List<DrawingCodeProcess> inProgressProcesses = new ArrayList<>();
                for (DrawingCodeProcess process : all) {
                        if (process.getProcessStatus() != 1) {
                                inProgressProcesses.add(process);
                        }
                }
                return inProgressProcesses.stream().map(process -> {
                        OrderDetailDto orderDetailDto = OrderDetailMapper.mapToOrderDetailDto(process.getOrderDetail());
                        Machine machine = process.getMachine();
                        MachineDto machineDto = (machine != null)
                                        ? MachineMapper.mapToMachineDto(machine)
                                        : null;
                        return DrawingCodeProcessMapper.toDto(orderDetailDto, machineDto, process);
                }).toList();
        }

        @Override
        public void recieveProcessFromTablet(String drawingCodeProcessId, Integer machineId, String staffId) {
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

                Staff staff = staffRepository.findById(staffId).orElseThrow(() -> new RuntimeException(
                                "Staff is not found:" + staffId));

                Protocol protocol = new Protocol();
                if (protocol.getId() == null
                                || protocol.getId().isEmpty()) {
                        String generatedId;
                        do {
                                generatedId = IdGenerator.generateRandomId();
                        } while (protocolRepository.existsById(generatedId));
                        protocol.setId(generatedId);
                }
                protocol.setProcess(process);
                protocol.setMachine(machine);
                protocol.setStaff(staff);
                protocolRepository.save(protocol);
                String sendMachine = "";
                if (machineId < 10) {
                        sendMachine = "0" + machineId;
                }
                LocalDateTime now = LocalDateTime.now();
                String formatted = now.format(DateTimeFormatter.ofPattern("MMddyyHH"));
                String payload = sendMachine + "-" + protocol.getId() + "-" + formatted;
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

}
