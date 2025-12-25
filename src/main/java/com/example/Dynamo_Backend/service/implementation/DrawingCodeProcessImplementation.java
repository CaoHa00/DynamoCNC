package com.example.Dynamo_Backend.service.implementation;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.config.MyWebSocketHandler;
import com.example.Dynamo_Backend.dto.*;
import com.example.Dynamo_Backend.dto.RequestDto.DrawingCodeProcessResquestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.CurrentStatusResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.ListCurrentStaffStatusDto;
import com.example.Dynamo_Backend.entities.*;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
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
        TempStartTimeRepository tempStartTimeRepository;
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
        AdminRepository adminRepository;
        CurrentStatusMapper currentStatusMapper;

        TempProcessRepository tempProcessRepository;

        // this api is for manager to add process(planned or not)
        @Override
        public DrawingCodeProcessDto addDrawingCodeProcess(DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                long createdTimestamp = System.currentTimeMillis();
                int status = 1;
                OrderDetail orderDetail = orderDetailRepository.findByOrderCode(drawingCodeProcessDto.getOrderCode())
                                .orElseThrow(() -> new BusinessException(
                                                "OrderDetail is not found:"
                                                                + drawingCodeProcessDto.getOrderCode() +
                                                                "Please check again!"));
                // Machine machine =
                // machineRepository.findById(drawingCodeProcessDto.getMachineId())
                // .orElseThrow(() -> new BusinessException("Machine is not found:" +
                // drawingCodeProcessDto.getMachineId()));
                DrawingCodeProcess drawingCodeProcess = DrawingCodeProcessMapper
                                .mapToDrawingCodeProcess(drawingCodeProcessDto);
                drawingCodeProcess.setOrderDetail(orderDetail);
                // drawingCodeProcess.setMachine(machine);
                drawingCodeProcess.setCreatedDate(createdTimestamp);
                drawingCodeProcess.setUpdatedDate(createdTimestamp);
                drawingCodeProcess.setStatus(status);
                drawingCodeProcess.setProcessStatus(1);
                drawingCodeProcess.setIsPlan(drawingCodeProcessDto.getIsPlan());
                // check if it is added by manager or staff
                if (drawingCodeProcess.getIsPlan() == 0) {
                        if (drawingCodeProcessDto.getMachineId() != null) {
                                Machine machine = machineRepository.findById(drawingCodeProcessDto.getMachineId())
                                                .orElse(null);
                                drawingCodeProcess.setMachine(machine);
                        }

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
                                .orElseThrow(() -> new BusinessException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));
                long updatedTimestamp = System.currentTimeMillis();
                Staff staff = staffRepository.findByStaffId(drawingCodeProcessDto.getStaffId())
                                .orElseThrow(() -> new BusinessException(
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
                                                .orElseThrow(() -> new BusinessException(
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

                Machine machine = machineRepository.findById(savedrawingCodeProcess.getMachine().getMachineId())
                                .orElse(null);

                boolean sent = sendMessageToMqtt(savedrawingCodeProcess, machine, staff);
                if (!sent) {
                        throw new BusinessException("Failed to send MQTT message");
                }
                return DrawingCodeProcessMapper.toDto(
                                OrderDetailMapper.mapToOrderDetailDto(drawingCodeProcess.getOrderDetail()),
                                MachineMapper.mapToMachineDto(drawingCodeProcess.getMachine()), savedrawingCodeProcess,
                                null, null, null);
        }

        @Override
        public DrawingCodeProcessDto getDrawingCodeProcessById(String drawingCodeProcessId) {
                DrawingCodeProcess drawingCodeProcess = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new BusinessException(
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
                                        List<StaffDto> staffDtos = (process.getOperateHistories() != null)
                                                        ? process.getOperateHistories().stream().map(operate -> {
                                                                Staff staff = staffRepository
                                                                                .findById(operate.getStaff().getId())
                                                                                .orElseThrow(() -> new BusinessException(
                                                                                                "Staff is not found for process: "
                                                                                                                + operate.getStaff()
                                                                                                                                .getId()));
                                                                return StaffMapper.mapStaffNameDto(staff);
                                                        }).toList()
                                                        : null;
                                        todoList.add(DrawingCodeProcessMapper.toDto(orderDetailDto, null, process,
                                                        staffDtos, planDto, null));
                                }
                        }
                        if (process.getProcessStatus() == 2
                                        && process.getMachine().getMachineId().equals(machineId)) {
                                orderDetailDto = OrderDetailMapper
                                                .mapToOrderDetailDto(process.getOrderDetail());
                                planDto = (process.getPlan() != null)
                                                ? PlanMapper.mapToPlanDto(process.getPlan())
                                                : null;
                                List<StaffDto> staffDtos = (process.getOperateHistories() != null)
                                                ? process.getOperateHistories().stream().map(operate -> {
                                                        Staff staff = staffRepository
                                                                        .findById(operate.getStaff().getId())
                                                                        .orElseThrow(() -> new BusinessException(
                                                                                        "Staff is not found for process: "
                                                                                                        + operate.getStaff()
                                                                                                                        .getId()));
                                                        return StaffMapper.mapStaffNameDto(staff);
                                                }).toList()
                                                : null;
                                TempProcess tempProcess = tempProcessRepository.findByProcessId(process.getProcessId());

                                inProgress = DrawingCodeProcessMapper.toDto(orderDetailDto, null, process,
                                                staffDtos, planDto, null);
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
                List<DrawingCodeProcess> processes = drawingCodeProcessRepository
                                .findByMachine_MachineIdAndStatus(machineId, 1);
                List<DrawingCodeProcess> currentProcess = new ArrayList<>();
                for (DrawingCodeProcess process : processes) {
                        if (process.getProcessStatus() == 2) {
                                currentProcess.add(process);
                        }
                }
                if (currentProcess.size() > 1) {
                        throw new BusinessException("Have more than 1 processes in progess!");
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
                                .orElseThrow(() -> new BusinessException(
                                                "DrawingCode Process is not found:" + drawingCodeProcessId));

                // Check if the process is recorded in other tables
                List<OperateHistory> operateHistories = operateHistoryRepository
                                .findByDrawingCodeProcess_processId(drawingCodeProcessId);
                if (!operateHistories.isEmpty()) {
                        throw new BusinessException(
                                        "Không thể xóa gia công đã được chạy");
                }

                ProcessTime processTime = processTimeRepository
                                .findByDrawingCodeProcess_ProcessId(drawingCodeProcessId);
                if (processTime != null) {
                        throw new BusinessException(
                                        "Không thể xóa gia công đã được chạy");
                }

                drawingCodeProcessRepository.delete(drawingCodeProcess);
        }

        // get all to do process
        @Override
        public List<DrawingCodeProcessDto> getAllDrawingCodeProcess() {
                List<DrawingCodeProcess> drawingCodeProcesses = drawingCodeProcessRepository.findByStatus(1);
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
                List<DrawingCodeProcess> all = drawingCodeProcessRepository.findByStatus(1);
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
                        List<StaffDto> staffDtos = (process.getOperateHistories() != null)
                                        ? process.getOperateHistories().stream().map(operate -> {
                                                Staff staff = staffRepository.findById(operate.getStaff().getId())
                                                                .orElseThrow(() -> new BusinessException(
                                                                                "Staff is not found for process: "
                                                                                                + operate.getStaff()
                                                                                                                .getId()));
                                                return StaffMapper.mapStaffNameDto(staff);
                                        }).toList()
                                        : null;
                        return DrawingCodeProcessMapper.toDto(orderDetailDto, machineDto, process, staffDtos, planDto,
                                        null);
                }).toList();
        }

        @Override
        public void receiveProcessFromTablet(String drawingCodeProcessId, Integer machineId, String staffId) {
                long timestampNow = System.currentTimeMillis();

                DrawingCodeProcess process = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new BusinessException(
                                                "DrawingCodeProcess is not found:" + drawingCodeProcessId));
                // check if there is any process in progress on this machine
                OrderDetail orderDetail = orderDetailRepository.findById(process.getOrderDetail().getOrderDetailId())
                                .orElse(null);
                if (orderDetail.getProgress() == 1) {
                        orderDetail.setProgress(2);
                        orderDetailRepository.save(orderDetail);
                }
                // check: just the empty machine(status=0) can start
                Machine machine = machineRepository.findById(machineId).orElseThrow(() -> new ResourceNotFoundException(
                                "Machine is not found:" + machineId));
                machine.setStatus(1);
                machineRepository.save(machine);
                process.setMachine(machine);
                process.setProcessStatus(2);
                CurrentStatus currentStatus = currentStatusRepository.findByMachineId(machineId);
                if (currentStatus.getStatus().contains("R") || currentStatus.getStatus().contains("S")) {
                        TempStartTime timeStartTime = tempStartTimeRepository.findByMachineId(machineId);
                        process.setStartTime(timeStartTime.getStartTime());
                        currentStatus.setProcessId(drawingCodeProcessId);
                        currentStatus.setStaffId(staffId);
                        currentStatusRepository.save(currentStatus);
                } else {
                        process.setStartTime(timestampNow);
                }

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

                Staff staff = staffRepository.findById(staffId).orElseThrow(() -> new ResourceNotFoundException(
                                "Staff is not found:" + staffId));
                // cập nhật staff đang làm
                CurrentStaffDto currentStaffDto = new CurrentStaffDto(null, staff.getId(), staff.getStaffId(),
                                machine.getMachineId(),
                                DateTimeUtil.convertTimestampToStringDate(timestampNow));
                currentStaffService.addCurrentStaff(currentStaffDto);

                if (machine.getMachineId() > 9) {
                        OperateHistory operateHistory = new OperateHistory(null,
                                        process.getManufacturingPoint(), 0,
                                        timestampNow, 0L, 1, staff, process);
                        operateHistoryRepository.save(operateHistory);
                }

                boolean sent = sendMessageToMqtt(process, machine, staff);

                // Get group by machine id in current year and month,if there is no current one,
                // get the nearest previous - Find in machine kpi
                int currentMonth = LocalDate.now().getMonthValue(); // 1 = January, 12 = December
                int currentYear = LocalDate.now().getYear();
                Group group = groupRepository.findLatestByMachineId(machineId, currentMonth, currentYear).orElse(null);
                List<CurrentStatus> currentStatuses = currentStatusRepository.findAll();
                List<CurrentStatusResponseDto> statusList = currentStatusService
                                .getCurrentStatusByGroupId(group.getGroupId());
                List<ListCurrentStaffStatusDto> listStaffStatus = currentStatusService
                                .getCurrentStaffStatusByGroupId(group.getGroupId());
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
                        MyWebSocketHandler.sendStaffStatusToClients(listStaffStatus);
                        MyWebSocketHandler.sendMachineStatusToClients(
                                        currentStatuses.stream().map(currentStatusMapper::mapToCurrentStatusDto)
                                                        .toList());

                } catch (IOException e) {
                        e.printStackTrace();
                        throw new BusinessException("Failed to send to user");
                }
                if (!sent) {
                        throw new BusinessException("Failed to send MQTT message");
                }
                currentStatusService.addCurrentStatuswhileActive(
                                (machine.getMachineId() - 1), process.getProcessId(), currentStaffDto.getStaffId());
        }

        @Override
        public DrawingCodeProcessDto addProcessByOperator(DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                long createdTimestamp = System.currentTimeMillis();
                int status = 1;
                // check if there is any process in progress on this machine
                List<DrawingCodeProcess> processes = drawingCodeProcessRepository
                                .findByMachine_MachineIdAndStatus(drawingCodeProcessDto.getMachineId(), 1);
                for (DrawingCodeProcess process2 : processes) {
                        if (process2.getProcessStatus() == 2) {
                                throw new BusinessException("There is already a process in progress on machine:"
                                                + drawingCodeProcessDto.getMachineId());
                        }
                }

                OrderDetail orderDetail = orderDetailRepository
                                .findByOrderCode(drawingCodeProcessDto.getOrderCode())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "OrderDetail is not found:"
                                                                + drawingCodeProcessDto.getOrderCode()));
                Machine machine = machineRepository.findById(drawingCodeProcessDto.getMachineId())
                                .orElseThrow(() -> new ResourceNotFoundException("Machine is not found:" +
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
                                .orElseThrow(() -> new ResourceNotFoundException(
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

                boolean sent = sendMessageToMqtt(savedrawingCodeProcess, machine, staff);

                int currentMonth = LocalDate.now().getMonthValue(); // 1 = January, 12 = December
                int currentYear = LocalDate.now().getYear();

                Group group = groupRepository.findLatestByMachineId(machine.getMachineId(), currentMonth, currentYear)
                                .orElse(null);
                List<CurrentStatusResponseDto> statusList = currentStatusService
                                .getCurrentStatusByGroupId(group.getGroupId());
                List<ListCurrentStaffStatusDto> listStaffStatus = currentStatusService
                                .getCurrentStaffStatusByGroupId(group.getGroupId());
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
                        MyWebSocketHandler.sendStaffStatusToClients(listStaffStatus);

                } catch (IOException e) {
                        e.printStackTrace();
                }
                if (!sent) {
                        throw new BusinessException("Failed to send MQTT message");
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
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "DrawingCodeProcess is not found:" + processId));
                OperateHistory operateHistory = operateHistoryRepository
                                .findByDrawingCodeProcess_processId(drawingCodeProcess.getProcessId())
                                .stream()
                                .filter(operate -> operate.getInProgress() == 1)
                                .findFirst()
                                .orElseGet(OperateHistory::new);
                Machine machine = machineRepository.findById(drawingCodeProcess.getMachine().getMachineId())
                                .orElseThrow(() -> new ResourceNotFoundException("Machine is not found:" +
                                                drawingCodeProcess.getMachine().getMachineId()));
                currentStatusService.addCurrentStatus(
                                (machine.getMachineId() - 1) + "-0");

                drawingCodeProcess.setProcessStatus(3);
                Long doneTime = System.currentTimeMillis();
                drawingCodeProcess.setEndTime(doneTime);
                drawingCodeProcess.setUpdatedDate(doneTime);

                if (operateHistory.getOperateHistoryId() != null) {
                        operateHistory.setStopTime(doneTime);
                        operateHistory.setInProgress(0);
                        operateHistoryRepository.save(operateHistory);
                }

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
                if (!sent) {
                        throw new BusinessException("Failed to send MQTT message");
                }

        }

        @Override
        public Page<DrawingCodeProcessResponseDto> getPlannedProcesses(Integer planned, int page, int size) {
                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "createdDate"));
                Page<DrawingCodeProcess> pageEntity = drawingCodeProcessRepository
                                .findByIsPlanAndProcessStatusNotAndStatus(
                                                planned,
                                                3, // processStatus != 3
                                                1, // status = 1
                                                pageable);
                return returnProcessDto(pageEntity);
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
                                                        .orElseThrow(() -> new ResourceNotFoundException(
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
                                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                                "Staff is not found for process: "
                                                                                                + operate.getStaff()
                                                                                                                .getId()));
                                                return StaffMapper.mapStaffNameDto(staff);
                                        }).distinct().toList()
                                        : null;
                        return DrawingCodeProcessMapper.toDto(orderDetailDto, machineDto, process, staffDtos, planDto,
                                        processTimeDto);
                }).toList();
        }

        public Page<DrawingCodeProcessResponseDto> returnProcessDto(
                        Page<DrawingCodeProcess> processes) {

                return processes.map(process -> {

                        OrderDetailDto orderDetailDto = OrderDetailMapper.mapOrderCodeDto(process.getOrderDetail());

                        MachineDto machineDto = process.getMachine() != null
                                        ? MachineMapper.mapOnlyMachineName(process.getMachine())
                                        : null;

                        PlanDto planDto = process.getPlan() != null
                                        ? PlanMapper.mapToPlanDto(process.getPlan())
                                        : null;

                        ProcessTimeDto processTimeDto = process.getProcessTime() != null
                                        ? ProcessTimeMapper.mapToProcessTimeDto(process.getProcessTime())
                                        : null;

                        List<StaffDto> staffDtos = process.getOperateHistories() != null
                                        ? process.getOperateHistories().stream()
                                                        .map(operate -> StaffMapper.mapStaffNameDto(operate.getStaff()))
                                                        .distinct()
                                                        .toList()
                                        : List.of();

                        return DrawingCodeProcessMapper.toDto(
                                        orderDetailDto,
                                        machineDto,
                                        process,
                                        staffDtos,
                                        planDto,
                                        processTimeDto);
                });
        }

        @Override
        public DrawingCodeProcessResponseDto updateProcessByOperator(String drawingCodeProcessId,
                        DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                long timestampNow = System.currentTimeMillis();
                DrawingCodeProcess process = drawingCodeProcessRepository.findById(drawingCodeProcessId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "DrawingCodeProcess is not found:" + drawingCodeProcessId));
                Machine machine = machineRepository.findById(process.getMachine().getMachineId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Machine is not found:" + process.getMachine().getMachineId()));

                CurrentStatus currentStatus = currentStatusRepository.findByMachineId(machine.getMachineId());
                System.out.println(currentStatus.getStatus());
                if (currentStatus.getStatus().equals("0")) {
                        OrderDetail orderDetail = orderDetailRepository
                                        .findByOrderCode(drawingCodeProcessDto.getOrderCode()).orElse(null);
                        process.setProcessType(drawingCodeProcessDto.getProcessType());
                        process.setPartNumber(drawingCodeProcessDto.getPartNumber());
                        process.setStepNumber(drawingCodeProcessDto.getStepNumber());
                        process.setOrderDetail(orderDetail);
                        process.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                        process.setPgTime(drawingCodeProcessDto.getPgTime());
                        drawingCodeProcessRepository.save(process);
                }

                Staff staff = staffRepository.findByStaffId(drawingCodeProcessDto.getStaffId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Staff is not found:"
                                                                + drawingCodeProcessDto.getStaffId()));
                TempProcess tempProcess = tempProcessRepository.findByProcessId(drawingCodeProcessId);
                CurrentStaff currentStaff = currentStaffRepository
                                .findByMachine_MachineId(machine.getMachineId());
                System.out.println(staff.getId());
                System.out.println(currentStaff.getStaff().getId());
                if (!staff.getId().equals(currentStaff.getStaff().getId()) || currentStatus.getStatus().equals("0")) {
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
                List<ListCurrentStaffStatusDto> listStaffStatus = currentStatusService
                                .getCurrentStaffStatusByGroupId(group.getGroupId());
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
                        MyWebSocketHandler.sendStaffStatusToClients(listStaffStatus);

                } catch (IOException e) {
                        e.printStackTrace();
                }
                if (!sent) {
                        throw new BusinessException("Failed to send MQTT message");
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

        @Override
        public List<DrawingCodeProcessResponseDto> getAll() {
                List<DrawingCodeProcess> all = drawingCodeProcessRepository.findByStatus(1);
                // List<DrawingCodeProcess> todoProcesses = new ArrayList<>();
                return all.stream().map(process -> {
                        OrderDetailDto orderDetailDto = OrderDetailMapper.mapToOrderDetailDto(process.getOrderDetail());
                        Machine machine = process.getMachine();
                        MachineDto machineDto = (machine != null)
                                        ? MachineMapper.mapToMachineDto(machine)
                                        : null;
                        PlanDto planDto = (process.getPlan() != null) ? PlanMapper.mapToPlanDto(process.getPlan())
                                        : null;

                        List<StaffDto> staffDtos = (process.getOperateHistories() != null)
                                        ? process.getOperateHistories().stream().map(operate -> {
                                                Staff staff = staffRepository.findById(operate.getStaff().getId())
                                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                                "Staff is not found for process: "
                                                                                                + operate.getStaff()
                                                                                                                .getId()));
                                                return StaffMapper.mapStaffNameDto(staff);
                                        }).distinct().toList()
                                        : null;
                        return DrawingCodeProcessMapper.toDto(orderDetailDto, machineDto, process, staffDtos, planDto,
                                        null);
                }).toList();
        }

        @Override
        public List<DrawingCodeProcessResponseDto> getCompletedProcessWithOperateHistoryData(String staffId, Long start,
                        Long stop) {
                List<DrawingCodeProcess> completedProcesses = drawingCodeProcessRepository
                                .findProcessesByStaffAndTimeRange(staffId, start, stop);
                return completedProcesses.stream().map(process -> {
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
                                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                                "Staff is not found for process: "
                                                                                                + operate.getStaff()
                                                                                                                .getId()));
                                                return StaffMapper.mapStaffNameDto(staff);
                                        }).toList()
                                        : null;

                        // Get all OperateHistory for this process and sum pgTime and manufacturingPoint
                        List<OperateHistory> histories = operateHistoryRepository
                                        .findByDrawingCodeProcess_processIdAndStaff_Id(process.getProcessId(), staffId);
                        Integer totalPgTime = histories.stream()
                                        .map(OperateHistory::getPgTime)
                                        .reduce(0, Integer::sum);
                        Integer totalManufacturingPoint = histories.stream()
                                        .map(OperateHistory::getManufacturingPoint)
                                        .reduce(0, Integer::sum);

                        DrawingCodeProcessResponseDto dto = DrawingCodeProcessMapper.toDto(orderDetailDto, machineDto,
                                        process, staffDtos, planDto, processTimeDto);

                        // Replace pgTime and manufacturingPoint with summed values from OperateHistory
                        dto.setPgTime(totalPgTime);
                        dto.setManufacturingPoint(totalManufacturingPoint);
                        return dto;
                }).toList();
        }

        @Override
        public void importExcel(MultipartFile file) {
                try {
                        InputStream inputStream = ((MultipartFile) file).getInputStream();
                        Workbook workbook = new XSSFWorkbook(inputStream);
                        Sheet sheet = workbook.getSheetAt(0);
                        List<String> orderCodes = new ArrayList<>();
                        boolean flag = true;
                        List<DrawingCodeProcessResquestDto> drawingCodeProcess = new ArrayList<>();
                        for (Row row : sheet) {
                                if (row.getRowNum() < 6)
                                        continue;

                                boolean missing = false;
                                for (int i = 2; i <= 8; i++) {
                                        if (row.getCell(i) == null) {
                                                missing = true;
                                                break;
                                        }
                                }
                                if (missing)
                                        continue;

                                DrawingCodeProcessResquestDto process = new DrawingCodeProcessResquestDto();
                                String processType = row.getCell(2).getStringCellValue();
                                String orderCode = row.getCell(3).getStringCellValue();
                                Integer partNumber = (int) row.getCell(4).getNumericCellValue();
                                Integer stepNumber = (int) row.getCell(5).getNumericCellValue();
                                Integer point = (int) row.getCell(6).getNumericCellValue();
                                Integer pgTime = (int) row.getCell(7).getNumericCellValue();
                                String startTime = row.getCell(8).getStringCellValue();
                                String endTime = row.getCell(9).getStringCellValue();
                                Integer staffId = (int) row.getCell(10).getNumericCellValue();
                                String machineName = row.getCell(11).getStringCellValue();
                                String userName = row.getCell(12).getStringCellValue();
                                if (!orderDetailRepository.existsByOrderCode(orderCode)) {
                                        flag = false;
                                        orderCodes.add(orderCode);
                                } else {
                                        process.setOrderCode(orderCode);
                                }
                                if (!machineRepository.existsByMachineName(machineName)) {
                                        throw new BusinessException("Máy " + machineName + " không tồn tại");
                                } else {
                                        Machine machine = machineRepository.findByMachineName(machineName).orElse(null);
                                        process.setMachineId(machine.getMachineId());
                                }
                                if (!staffRepository.existsByStaffId(staffId)) {
                                        throw new BusinessException("Nhân viên " + staffId + " không tồn tại");
                                } else {
                                        process.setStaffId(staffId);
                                }
                                process.setProcessType(processType);
                                process.setPartNumber(partNumber);
                                process.setStepNumber(stepNumber);
                                process.setManufacturingPoint(point);
                                process.setPgTime(pgTime);
                                process.setStartTime(startTime);
                                process.setEndTime(endTime);
                                process.setIsPlan(1);
                                Admin admin = adminRepository.findByUsername(userName).orElse(null);
                                process.setPlannerId(admin.getId());
                                drawingCodeProcess.add(process);
                        }
                        if (flag == false) {
                                throw new BusinessException("ID mã hàng " + orderCodes + " không tồn tại");
                        }
                        addDrawingCodeProcess(drawingCodeProcess);
                        workbook.close();
                        inputStream.close();

                } catch (Exception e) {
                        throw new BusinessException("Failed to import group KPI from Excel file: " + e.getMessage());
                }
        }

        @Override
        public void addDrawingCodeProcess(List<DrawingCodeProcessResquestDto> drawingCodeProcessDto) {
                for (DrawingCodeProcessResquestDto drawingCodeProcessResquestDto : drawingCodeProcessDto) {
                        addDrawingCodeProcess(drawingCodeProcessResquestDto);
                }
        }

        @Override
        public DrawingCodeProcessDto updateDrawingCodeProcess(DrawingCodeProcessResquestDto drawingCodeProcessDto) {

                DrawingCodeProcess process = drawingCodeProcessRepository.findById(drawingCodeProcessDto.getProcessId())
                                .orElseThrow(() -> new BusinessException(
                                                "DrawingCode Process is not found: "
                                                                + drawingCodeProcessDto.getProcessId()));

                if (drawingCodeProcessDto.getManufacturingPoint() == null) {
                        throw new BusinessException("Điểm trống! Vui lòng nhập điểm");
                }
                if (drawingCodeProcessDto.getPgTime() == null) {
                        throw new BusinessException("Giờ PG trống! Vui lòng nhập giờ");
                }
                process.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                process.setPgTime(drawingCodeProcessDto.getPgTime());
                Integer machineId = drawingCodeProcessDto.getMachineId();
                Integer resolvedMachineId;

                if (machineId == null) {
                        if (process.getMachine() != null) {
                                resolvedMachineId = process.getMachine().getMachineId();
                        } else {
                                throw new BusinessException("Máy trống! Vui lòng chọn máy");
                        }
                } else {
                        resolvedMachineId = machineId;
                }

                // Lấy machine
                Machine machine = machineRepository.findById(resolvedMachineId)
                                .orElseThrow(() -> new BusinessException(
                                                "Không tìm thấy máy với ID: " + resolvedMachineId));

                process.setMachine(machine);

                DrawingCodeProcess saved = drawingCodeProcessRepository.save(process);
                return DrawingCodeProcessMapper.mapToDrawingCodeProcessDto(saved);

        }

        @Override
        public List<DrawingCodeProcessDto> getProcessesByOrderDetail(String orderDetailId) {
                List<DrawingCodeProcess> drawingCodeProcess = drawingCodeProcessRepository
                                .findByOrderDetail_OrderDetailIdAndStatusAndProcessStatusNot(orderDetailId, 1, 3);

                return drawingCodeProcess.stream().map(DrawingCodeProcessMapper::mapToDrawingCodeProcessDto).toList();
        }

        @Override
        public void updateProcessStatus(String orderDetailId) {
                List<DrawingCodeProcess> drawingCodeProcesses = drawingCodeProcessRepository
                                .findByOrderDetail_OrderDetailIdAndStatusAndProcessStatusNot(orderDetailId, 1, 0);
                for (DrawingCodeProcess drawingCodeProcess : drawingCodeProcesses) {
                        drawingCodeProcess.setStatus(0);
                        drawingCodeProcessRepository.save(drawingCodeProcess);
                }

        }

}
