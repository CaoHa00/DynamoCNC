package com.example.Dynamo_Backend.service.implementation;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import com.example.Dynamo_Backend.config.MyWebSocketHandler;
import com.example.Dynamo_Backend.dto.*;
import com.example.Dynamo_Backend.dto.RequestDto.DrawingCodeProcessResquestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.CurrentStatusResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;
import com.example.Dynamo_Backend.entities.*;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.mapper.*;
import com.example.Dynamo_Backend.repository.*;
import com.example.Dynamo_Backend.service.*;
import com.example.Dynamo_Backend.util.DateTimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        ProcessTimeRepository processTimeRepository;
        PlanRepository planRepository;
        private CurrentStatusService currentStatusService;
        GroupRepository groupRepository;
        ProcessTimeSummaryService processTimeSummaryService;
        ProcessTimeService processTimeService;
        CurrentStaffRepository currentStaffRepository;
        CurrentStatusRepository currentStatusRepository;
        OperateHistoryService operateHistoryService;

        TempProcessRepository tempProcessRepository;

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
                drawingCodeProcess.setIsPlan(0);
                // check if it is added by manager or staff
                if (drawingCodeProcess.getIsPlan() == 0) {
                        Machine machine = machineRepository.findById(drawingCodeProcessDto.getMachineId()).orElse(null);
                        drawingCodeProcess.setMachine(machine);
                }
                DrawingCodeProcess savedrawingCodeProcess = drawingCodeProcessRepository.save(drawingCodeProcess);
                if (savedrawingCodeProcess.getIsPlan() == 1) {
                        PlanDto plan = DrawingCodeProcessMapper.mapToPlanDto(drawingCodeProcessDto.getProcessId(),
                                        drawingCodeProcessDto);
                        plan.setProcessId(savedrawingCodeProcess.getProcessId());
                        PlanDto planDto = planService.addPlan(plan);
                        savedrawingCodeProcess.setPlan(PlanMapper.mapToPlan(planDto));
                }
                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(savedrawingCodeProcess);
        }

        @Override
        public DrawingCodeProcessResponseDto updateDrawingCodeProcess(String drawingCodeProcessId,
                        DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));
                long updatedTimestamp = System.currentTimeMillis();
                Staff staff = staffRepository.findByStaffId(drawingCodeProcessDto.getStaffId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Staff is not found:" + drawingCodeProcessDto.getStaffId()));

                if (drawingCodeProcessDto.getMachineId() != null && drawingCodeProcessDto.getMachineId() > 9) {
                        CurrentStaffDto oldCurrentStaff = currentStaffService.getCurrentStaffByMachineId(
                                        drawingCodeProcess.getMachine().getMachineId());
                        if (oldCurrentStaff != null && oldCurrentStaff.getStaffId() != null
                                        && !oldCurrentStaff.getStaffId().equals(staff.getId())) {
                                OperateHistory operateHistory = operateHistoryRepository
                                                .findByDrawingCodeProcess_processId(drawingCodeProcessId)
                                                .stream()
                                                .filter(operate -> operate.getInProgress() == 1)
                                                .findFirst()
                                                .orElseGet(OperateHistory::new);
                                if (operateHistory.getOperateHistoryId() != null) {
                                        operateHistory.setStopTime(updatedTimestamp);
                                        operateHistory.setInProgress(0);
                                        operateHistoryRepository.save(operateHistory);
                                        OperateHistory newOperateHistory = new OperateHistory(null,
                                                        drawingCodeProcess.getManufacturingPoint(),
                                                        drawingCodeProcess.getPgTime(),
                                                        updatedTimestamp, 0L, 1, staff, drawingCodeProcess);
                                        operateHistoryRepository.save(newOperateHistory);
                                }
                        }
                }

                CurrentStaffDto currentStaffDto = new CurrentStaffDto(null, staff.getId(),
                                staff.getStaffId(),
                                drawingCodeProcess.getMachine().getMachineId(),
                                DateTimeUtil.convertTimestampToStringDate(updatedTimestamp));
                currentStaffService.addCurrentStaff(currentStaffDto);
                drawingCodeProcess.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                drawingCodeProcess.setUpdatedDate(updatedTimestamp);

                OrderDetail orderDetail = drawingCodeProcessDto.getOrderCode() != null
                                ? orderDetailRepository.findByOrderCode(drawingCodeProcessDto.getOrderCode())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "OrderDetail is not found:"
                                                                                + drawingCodeProcessDto
                                                                                                .getOrderCode()))
                                : null;
                if (orderDetail != null) {
                        drawingCodeProcess.setOrderDetail(orderDetail);
                }
                if (drawingCodeProcessDto.getPartNumber() != null) {
                        drawingCodeProcess.setPartNumber(drawingCodeProcessDto.getPartNumber());
                }
                if (drawingCodeProcessDto.getStepNumber() != null) {
                        drawingCodeProcess.setStepNumber(drawingCodeProcessDto.getStepNumber());
                }
                if (drawingCodeProcessDto.getPgTime() != null) {
                        drawingCodeProcess.setPgTime(drawingCodeProcessDto.getPgTime());
                }

                DrawingCodeProcess savedrawingCodeProcess = drawingCodeProcessRepository.save(drawingCodeProcess);

                String sendMachine = "";
                if (savedrawingCodeProcess.getMachine().getMachineId() < 10) {
                        sendMachine = "0" + (savedrawingCodeProcess.getMachine().getMachineId() - 1);
                }
                Integer productStatus;
                switch (savedrawingCodeProcess.getProcessType()) {
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
                String payload = sendMachine + "*" + staff.getStaffId() + "*" + productStatus + "*"
                                + savedrawingCodeProcess.getOrderDetail().getOrderCode() + "*"
                                + savedrawingCodeProcess.getPartNumber() + "*"
                                + savedrawingCodeProcess.getStepNumber() + "*"
                                + savedrawingCodeProcess.getManufacturingPoint() + "*"
                                + savedrawingCodeProcess.getPgTime();
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

                return DrawingCodeProcessMapper.toDto(
                                OrderDetailMapper.mapToOrderDetailDto(drawingCodeProcess.getOrderDetail()),
                                MachineMapper.mapToMachineDto(drawingCodeProcess.getMachine()), savedrawingCodeProcess,
                                null, null, null);
        }

        @Override
        public DrawingCodeProcessDto getDrawingCodeProcessById(String drawingCodeProcessId) {
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));
                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(drawingCodeProcess);
        }

        // get all process by machineId for **tablet**
        @Override
        public Map<String, Object> getDrawingCodeProcessByMachineId(Integer machineId) {
                List<DrawingCodeProcess> processes = drawingCodeProcessRepository.findByMachineOrPlanMachine(machineId);
                List<DrawingCodeProcessResponseDto> todoList = new ArrayList<>();
                DrawingCodeProcessResponseDto inProgress = null;
                OrderDetailDto orderDetailDto;
                PlanDto planDto;
                Map<String, Object> result = new HashMap<>();
                for (DrawingCodeProcess process : processes) {
                        if (process.getProcessStatus() == 1) {
                                if (process.getIsPlan() == 0 || process.getIsPlan() == 1) {
                                        orderDetailDto = OrderDetailMapper
                                                        .mapToOrderDetailDto(process.getOrderDetail());
                                        planDto = (process.getPlan() != null)
                                                        ? PlanMapper.mapToPlanDto(process.getPlan())
                                                        : null;
                                        todoList.add(DrawingCodeProcessMapper.toDto(orderDetailDto, null, process,
                                                        null, planDto, null));
                                }
                        }
                        if (process.getProcessStatus() == 2
                                        && process.getMachine().getMachineId().equals(machineId)) {
                                orderDetailDto = OrderDetailMapper
                                                .mapToOrderDetailDto(process.getOrderDetail());
                                planDto = (process.getPlan() != null)
                                                ? PlanMapper.mapToPlanDto(process.getPlan())
                                                : null;
                                TempProcess tempProcess = tempProcessRepository.findByProcessId(process.getProcessId());

                                inProgress = DrawingCodeProcessMapper.toDto(orderDetailDto, null, process,
                                                null, planDto, null);
                                inProgress.setManufacturingPoint(tempProcess.getPoint());
                                inProgress.setPgTime(tempProcess.getPgTime());

                        }

                        result.put("todo", todoList);
                        result.put("inProgress", inProgress);
                }
                return result;
        }

        @Override
        public DrawingCodeProcessDto getProcessDtoByMachineId(Integer machineId) {
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
        public List<DrawingCodeProcessResponseDto> getAllTodoProcesses() {
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
                        PlanDto planDto = (process.getPlan() != null) ? PlanMapper.mapToPlanDto(process.getPlan())
                                        : null;
                        return DrawingCodeProcessMapper.toDto(orderDetailDto, machineDto, process, null, planDto, null);
                }).toList();
        }

        @Override
        public void receiveProcessFromTablet(String drawingCodeProcessId, Integer machineId, String staffId) {
                long timestampNow = System.currentTimeMillis();

                DrawingCodeProcess process = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCodeProcess is not found:" + drawingCodeProcessId));
                // check if there is any process in progress on this machine
                List<DrawingCodeProcess> processes = drawingCodeProcessRepository.findByMachine_MachineId(machineId);
                for (DrawingCodeProcess process2 : processes) {
                        if (process2.getProcessStatus() == 2) {
                                throw new BusinessException("There is already a process in progress on this machine.");
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

                TempProcess tempProcess = tempProcessRepository.findByMachineId(machineId);
                if (tempProcess == null) {
                        tempProcess = new TempProcess();
                }
                tempProcess.setMachineId(machineId);
                tempProcess.setProcessId(drawingCodeProcessId);
                tempProcess.setPgTime(process.getPgTime());
                tempProcess.setPoint(process.getManufacturingPoint());

                tempProcessRepository.save(tempProcess);

                Staff staff = staffRepository.findById(staffId).orElseThrow(() -> new RuntimeException(
                                "Staff is not found:" + staffId));
                // cập nhật staff đang làm
                CurrentStaffDto currentStaffDto = new CurrentStaffDto(null, staff.getId(), staff.getStaffId(),
                                machine.getMachineId(),
                                DateTimeUtil.convertTimestampToStringDate(timestampNow));
                currentStaffService.addCurrentStaff(currentStaffDto);

                if (machine.getMachineId() > 9) {
                        OperateHistory operateHistory = new OperateHistory(null,
                                        process.getManufacturingPoint(), 0f,
                                        timestampNow, 0L, 1, staff, process);
                        operateHistoryRepository.save(operateHistory);
                }

                boolean sent = sendMessageToMqtt(process, machine, staff);

                // Get group by machine id in current year and month,if there is no current one,
                // get the nearest previous - Find in machine kpi
                int currentMonth = LocalDate.now().getMonthValue(); // 1 = January, 12 = December
                int currentYear = LocalDate.now().getYear();
                Group group = groupRepository.findLatestByMachineId(machineId, currentMonth, currentYear).orElse(null);

                List<CurrentStatusResponseDto> statusList = currentStatusService
                                .getCurrentStatusByGroupId(group.getGroupId());
                try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        String jsonMessage = objectMapper.writeValueAsString(
                                        new java.util.HashMap<String, Object>() {
                                                {
                                                        put("type", group.getGroupName()
                                                                        .concat("-status"));
                                                        put("data", statusList);
                                                }
                                        });
                        MyWebSocketHandler.sendGroupStatusToClients(jsonMessage);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                if (sent) {
                        System.out.println("Message sent successfully: ");
                } else {
                        System.err.println("Failed to send message: ");
                }
                currentStatusService.addCurrentStatus(
                                (machine.getMachineId() - 1) + "-0");
        }

        @Override
        public DrawingCodeProcessDto addProcessByOperator(DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                long createdTimestamp = System.currentTimeMillis();
                int status = 1;
                // check if there is any process in progress on this machine
                List<DrawingCodeProcess> processes = drawingCodeProcessRepository
                                .findByMachine_MachineId(drawingCodeProcessDto.getMachineId());
                for (DrawingCodeProcess process2 : processes) {
                        if (process2.getProcessStatus() == 2) {
                                throw new BusinessException("There is already a process in progress on machine:"
                                                + drawingCodeProcessDto.getMachineId());
                        }
                }

                OrderDetail orderDetail = orderDetailRepository
                                .findByOrderCode(drawingCodeProcessDto.getOrderCode())
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
                CurrentStaffDto currentStaffDto = new CurrentStaffDto(null, staff.getId(), staff.getStaffId(),
                                machine.getMachineId(),
                                DateTimeUtil.convertTimestampToStringDate(createdTimestamp));
                currentStaffService.addCurrentStaff(currentStaffDto);

                machine.setStatus(1);
                machineRepository.save(machine);

                if (machine.getMachineId() > 9) {
                        OperateHistory operateHistory = new OperateHistory(null,
                                        savedrawingCodeProcess.getManufacturingPoint(),
                                        savedrawingCodeProcess.getPgTime(),
                                        createdTimestamp, 0L, 1, staff, savedrawingCodeProcess);
                        operateHistoryRepository.save(operateHistory);
                }

                // List<CurrentStatusResponseDto> statusList = currentStatusService
                // .getCurrentStatusByGroupId(machine.getGroup().getGroupId());
                // try {
                // ObjectMapper objectMapper = new ObjectMapper();
                // String jsonMessage = objectMapper.writeValueAsString(
                // new java.util.HashMap<String, Object>() {
                // {
                // put("type", machine.getGroup().getGroupName()
                // .concat("-status"));
                // put("data", statusList);
                // }
                // });
                // MyWebSocketHandler.sendGroupStatusToClients(jsonMessage);
                // } catch (IOException e) {
                // e.printStackTrace();
                // }

                boolean sent = sendMessageToMqtt(savedrawingCodeProcess, machine, staff);

                int currentMonth = LocalDate.now().getMonthValue(); // 1 = January, 12 = December
                int currentYear = LocalDate.now().getYear();

                Group group = groupRepository.findLatestByMachineId(machine.getMachineId(), currentMonth, currentYear)
                                .orElse(null);
                List<CurrentStatusResponseDto> statusList = currentStatusService
                                .getCurrentStatusByGroupId(group.getGroupId());
                try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        String jsonMessage = objectMapper.writeValueAsString(
                                        new java.util.HashMap<String, Object>() {
                                                {
                                                        put("type", group.getGroupName()
                                                                        .concat("-status"));
                                                        put("data", statusList);
                                                }
                                        });
                        MyWebSocketHandler.sendGroupStatusToClients(jsonMessage);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                if (sent) {
                        System.out.println("Message sent successfully: " + "0");
                } else {
                        System.err.println("Failed to send message: " + "1");
                }

                currentStatusService.addCurrentStatus(
                                (machine.getMachineId() - 1) + "-0");

                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(savedrawingCodeProcess);

        }

        public boolean sendMessageToMqtt(DrawingCodeProcess savedrawingCodeProcess, Machine machine, Staff staff) {

                TempProcess tempProcess = tempProcessRepository.findByProcessId(savedrawingCodeProcess.getProcessId());
                String sendMachine = "";
                if (machine.getMachineId() < 10) {
                        sendMachine = "0" + (machine.getMachineId() - 1);
                }
                Integer productStatus;
                switch (savedrawingCodeProcess.getProcessType()) {
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
                String payload = sendMachine + "*" + staff.getStaffId() + "*" + productStatus + "*"
                                + savedrawingCodeProcess.getOrderDetail().getOrderCode() + "*"
                                + savedrawingCodeProcess.getPartNumber() + "*"
                                + savedrawingCodeProcess.getStepNumber() + "*"
                                + tempProcess.getPoint() + "*"
                                + tempProcess.getPgTime();
                Message<String> message = MessageBuilder
                                .withPayload(payload)
                                .setHeader("mqtt_topic", "myTopic")
                                .build();
                boolean sent = mqttOutboundChannel.send(message);
                return sent;
        }

        // reset lai currentStatus -- chua lam
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
                                .orElseGet(OperateHistory::new);
                drawingCodeProcess.setProcessStatus(3);
                drawingCodeProcess.setEndTime(doneTime);
                drawingCodeProcess.setUpdatedDate(doneTime);

                if (operateHistory.getOperateHistoryId() != null) {
                        operateHistory.setStopTime(doneTime);
                        operateHistory.setInProgress(0);
                        operateHistoryRepository.save(operateHistory);
                }

                Machine machine = machineRepository.findById(drawingCodeProcess.getMachine().getMachineId())
                                .orElseThrow(() -> new RuntimeException("Machine is not found:" +
                                                drawingCodeProcess.getMachine().getMachineId()));
                machine.setStatus(0);

                // calculate processTime
                processTimeService.calculateProcessTime(drawingCodeProcess);

                processTimeSummaryService
                                .sumTimesByOrderDetailId(drawingCodeProcess.getOrderDetail().getOrderDetailId());

                CurrentStaffDto currentStaffDto = currentStaffService
                                .getCurrentStaffByMachineId(machine.getMachineId());

                currentStaffService.deleteCurrentStaff(currentStaffDto.getId());
                drawingCodeProcessRepository.save(drawingCodeProcess);
                machineRepository.save(machine);

                String sendMachine = "";
                if (machine.getMachineId() < 10) {
                        sendMachine = "0" + (machine.getMachineId() - 1);
                }
                String payload = sendMachine + "*#";
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

                currentStatusService.addCurrentStatus(
                                (machine.getMachineId() - 1) + "-0");
        }

        @Override
        public List<DrawingCodeProcessResponseDto> getPlannedProcesses(Integer planned) {
                List<DrawingCodeProcess> all = drawingCodeProcessRepository.findByIsPlanAndProcessStatusNot(planned, 3);
                return returnProcessDto(all);
        }

        @Override
        public DrawingCodeProcessResponseDto updateProcessByAdmin(String drawingCodeProcessId,
                        DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                DrawingCodeProcess drawingCodeProcess = DrawingCodeProcessMapper
                                .mapToDrawingCodeProcess(drawingCodeProcessDto);
                DrawingCodeProcess process = drawingCodeProcessRepository.findById(drawingCodeProcessId).orElse(null);
                Machine machine = machineRepository.findById(drawingCodeProcessDto.getMachineId()).orElse(null);

                OrderDetail orderDetail = orderDetailRepository.findByOrderCode(drawingCodeProcessDto.getOrderCode())
                                .orElse(null);
                process.setOrderDetail(orderDetail);
                process.setPartNumber(drawingCodeProcess.getPartNumber());
                process.setStepNumber(drawingCodeProcess.getStepNumber());
                process.setManufacturingPoint(drawingCodeProcess.getManufacturingPoint());
                process.setStatus(drawingCodeProcess.getStatus());
                process.setProcessType(drawingCodeProcess.getProcessType());
                process.setPgTime(drawingCodeProcess.getPgTime());
                process.setIsPlan(1);
                process.setProcessStatus(process.getProcessStatus());
                PlanDto planDto = DrawingCodeProcessMapper.mapToPlanDto(drawingCodeProcessId, drawingCodeProcessDto);
                drawingCodeProcessRepository.save(process);
                Plan plan = planRepository.findByDrawingCodeProcess_ProcessId(drawingCodeProcessId);
                planService.updatePlan(plan.getId(), planDto);
                ProcessTimeDto processTimeDto = (drawingCodeProcess.getProcessTime() != null)
                                ? ProcessTimeMapper.mapToProcessTimeDto(drawingCodeProcess.getProcessTime())
                                : null;
                List<StaffDto> staffDtos = (drawingCodeProcess.getOperateHistories() != null)
                                ? drawingCodeProcess.getOperateHistories().stream().map(operate -> {
                                        Staff newStaff = staffRepository.findById(operate.getStaff().getId())
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Staff is not found for process: "
                                                                                        + operate.getStaff()
                                                                                                        .getId()));
                                        return StaffMapper.mapToStaffDto(newStaff);
                                }).toList()
                                : null;
                return DrawingCodeProcessMapper.toDto(OrderDetailMapper.mapOrderCodeDto(orderDetail),
                                MachineMapper.mapOnlyMachineName(machine), drawingCodeProcess, staffDtos, planDto,
                                processTimeDto);

        }

        @Override
        public List<DrawingCodeProcessResponseDto> getCompletedProcess(Integer status, Long start, Long stop) {
                List<DrawingCodeProcess> all = drawingCodeProcessRepository.findByStatusAndTimeRange(3, start, stop);
                return returnProcessDto(all);
        }

        @Override
        public List<DrawingCodeProcessResponseDto> getProcessesByOperator(String staffId, Long start, Long stop) {
                List<DrawingCodeProcess> all = drawingCodeProcessRepository.findProcessesByStaffAndTimeRange(staffId,
                                start, stop);

                return returnProcessDto(all);
        }

        @Override
        public List<DrawingCodeProcessResponseDto> getProcessByMachine(Integer machineId, Long start, Long stop) {

                List<DrawingCodeProcess> all = drawingCodeProcessRepository.findCompletedProcessesByMachineAndTime(
                                machineId,
                                start, stop);
                return returnProcessDto(all);
        }

        public List<DrawingCodeProcessResponseDto> returnProcessDto(List<DrawingCodeProcess> processes) {
                return processes.stream().map(process -> {
                        OrderDetailDto orderDetailDto = OrderDetailMapper.mapOrderCodeDto(process.getOrderDetail());
                        Machine machine = process.getMachine();
                        MachineDto machineDto = (machine != null)
                                        ? MachineMapper.mapOnlyMachineName(machine)
                                        : null;
                        PlanDto planDto = (process.getPlan() != null) ? PlanMapper.mapToPlanDto(process.getPlan())
                                        : null;
                        ProcessTimeDto processTimeDto = (process.getProcessTime() != null)
                                        ? ProcessTimeMapper.mapToProcessTimeDto(process.getProcessTime())
                                        : null;
                        List<StaffDto> staffDtos = (process.getOperateHistories() != null)
                                        ? process.getOperateHistories().stream().map(operate -> {
                                                Staff staff = staffRepository.findById(operate.getStaff().getId())
                                                                .orElseThrow(() -> new RuntimeException(
                                                                                "Staff is not found for process: "
                                                                                                + operate.getStaff()
                                                                                                                .getId()));
                                                return StaffMapper.mapStaffNameDto(staff);
                                        }).toList()
                                        : null;
                        return DrawingCodeProcessMapper.toDto(orderDetailDto, machineDto, process, staffDtos, planDto,
                                        processTimeDto);
                }).toList();
        }

        @Override
        public DrawingCodeProcessResponseDto updateProcessByOperator(String drawingCodeProcessId,
                        DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                long timestampNow = System.currentTimeMillis();
                DrawingCodeProcess process = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new RuntimeException(
                                                "DrawingCodeProcess is not found:" + drawingCodeProcessId));
                Machine machine = machineRepository.findById(process.getMachine().getMachineId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Machine is not found:" + process.getMachine().getMachineId()));

                CurrentStatus currentStatus = currentStatusRepository.findByMachineId(machine.getMachineId());
                System.out.println(currentStatus.getStatus());
                if (currentStatus.getStatus().equals("0")) {
                        process.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                        process.setPgTime(drawingCodeProcessDto.getPgTime());
                        drawingCodeProcessRepository.save(process);
                }

                Staff staff = staffRepository.findByStaffId(drawingCodeProcessDto.getStaffId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Staff is not found:"
                                                                + drawingCodeProcessDto.getStaffId()));
                TempProcess tempProcess = tempProcessRepository.findByProcessId(drawingCodeProcessId);
                CurrentStaff currentStaff = currentStaffRepository
                                .findByMachine_MachineId(machine.getMachineId());
                System.out.println(staff.getId());
                System.out.println(currentStaff.getStaff().getId());
                if (!staff.getId().equals(currentStaff.getStaff().getId())) {
                        tempProcess.setPgTime(drawingCodeProcessDto.getPgTime());
                        tempProcess.setPoint(drawingCodeProcessDto.getManufacturingPoint());
                        tempProcess.setMachineId(machine.getMachineId());
                        tempProcessRepository.save(tempProcess);
                }
                // cập nhật staff đang làm
                CurrentStaffDto currentStaffDto = new CurrentStaffDto(null, staff.getId(), staff.getStaffId(),
                                machine.getMachineId(),
                                DateTimeUtil.convertTimestampToStringDate(timestampNow));
                currentStaffService.addCurrentStaff(currentStaffDto);

                if (machine.getMachineId() > 9) {
                        OperateHistory operateHistory = new OperateHistory(null,
                                        process.getManufacturingPoint(), process.getPgTime(),
                                        timestampNow, 0L, 1, staff, process);
                        operateHistoryRepository.save(operateHistory);
                }

                boolean sent = sendMessageToMqtt(process, machine, staff);

                // Get group by machine id in current year and month,if there is no current one,
                // get the nearest previous - Find in machine kpi
                int currentMonth = LocalDate.now().getMonthValue(); // 1 = January, 12 = December
                int currentYear = LocalDate.now().getYear();
                Group group = groupRepository
                                .findLatestByMachineId(process.getMachine().getMachineId(), currentMonth, currentYear)
                                .orElse(null);

                List<CurrentStatusResponseDto> statusList = currentStatusService
                                .getCurrentStatusByGroupId(group.getGroupId());
                try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        String jsonMessage = objectMapper.writeValueAsString(
                                        new java.util.HashMap<String, Object>() {
                                                {
                                                        put("type", group.getGroupName()
                                                                        .concat("-status"));
                                                        put("data", statusList);
                                                }
                                        });
                        MyWebSocketHandler.sendGroupStatusToClients(jsonMessage);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                if (sent) {
                        System.out.println("Message sent successfully: ");
                } else {
                        System.err.println("Failed to send message: ");
                }

                DrawingCodeProcessResponseDto response = DrawingCodeProcessMapper.toDto(
                                OrderDetailMapper.mapToOrderDetailDto(process.getOrderDetail()),
                                MachineMapper.mapToMachineDto(process.getMachine()),
                                process, // use the original process (unchanged)
                                null, null, null);
                response.setPgTime(tempProcess.getPgTime());
                response.setManufacturingPoint(tempProcess.getPoint());

                if (currentStatus.getStatus().contains("R")) {
                        String payload = "0" + (currentStatus.getMachineId() - 1) + "-" + currentStatus.getStatus();
                        operateHistoryService.addOperateHistory(payload);

                }
                return response;
        }
}
