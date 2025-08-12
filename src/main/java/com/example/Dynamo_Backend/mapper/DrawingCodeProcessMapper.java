package com.example.Dynamo_Backend.mapper;

import java.util.List;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.dto.PlanDto;
import com.example.Dynamo_Backend.dto.ProcessTimeDto;
import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.dto.RequestDto.DrawingCodeProcessResquestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class DrawingCodeProcessMapper {
        public static DrawingCodeProcess mapToDrawingCodeProcess(DrawingCodeProcessDto drawingCodeProcessDto) {
                DrawingCodeProcess drawingCodeProcess = new DrawingCodeProcess();

                drawingCodeProcess.setProcessId(drawingCodeProcessDto.getProcessId());
                drawingCodeProcess.setPartNumber(drawingCodeProcessDto.getPartNumber());
                drawingCodeProcess.setStepNumber(drawingCodeProcessDto.getStepNumber());
                drawingCodeProcess.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                drawingCodeProcess.setProcessType(drawingCodeProcessDto.getProcessType());
                drawingCodeProcess.setProcessStatus(drawingCodeProcessDto.getProcessStatus());
                drawingCodeProcess.setPgTime(drawingCodeProcessDto.getPgTime());
                // drawingCodeProcess.setPgRunTime(drawingCodeProcessDto.getPgRunTime());
                // drawingCodeProcess.setOffsetRunTime(drawingCodeProcessDto.getOffsetRunTime());
                // drawingCodeProcess.setTotalRunningTime(drawingCodeProcessDto.getTotalRunningTime());
                // drawingCodeProcess.setTotalStopTime(drawingCodeProcessDto.getTotalStopTime());
                drawingCodeProcess.setStatus(drawingCodeProcessDto.getStatus());
                drawingCodeProcess.setOperateHistories(drawingCodeProcessDto.getStaffHistories());
                drawingCodeProcess.setLogs(drawingCodeProcessDto.getStatstistics());
                drawingCodeProcess.setCreatedDate(0);
                drawingCodeProcess.setUpdatedDate(0);
                drawingCodeProcess.setStartTime(
                                DateTimeUtil.convertStringToTimestamp(drawingCodeProcessDto.getStartTime()));
                drawingCodeProcess
                                .setEndTime(DateTimeUtil.convertStringToTimestamp(drawingCodeProcessDto.getEndTime()));
                drawingCodeProcess.setIsPlan(drawingCodeProcessDto.getIsPlan());
                return drawingCodeProcess;
        }

        public static DrawingCodeProcess mapToDrawingCodeProcess(DrawingCodeProcessResquestDto drawingCodeProcessDto) {
                DrawingCodeProcess drawingCodeProcess = new DrawingCodeProcess();

                drawingCodeProcess.setProcessId(drawingCodeProcessDto.getProcessId());
                drawingCodeProcess.setPartNumber(drawingCodeProcessDto.getPartNumber());
                drawingCodeProcess.setStepNumber(drawingCodeProcessDto.getStepNumber());
                drawingCodeProcess.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                drawingCodeProcess.setStatus(drawingCodeProcessDto.getStatus());
                drawingCodeProcess.setProcessStatus(drawingCodeProcessDto.getInProgress());
                drawingCodeProcess.setProcessType(drawingCodeProcessDto.getProcessType());
                drawingCodeProcess.setPgTime(drawingCodeProcessDto.getPgTime());
                // drawingCodeProcess.setStartTime(
                // DateTimeUtil.convertStringToTimestamp(drawingCodeProcessDto.getStartTime()));
                // drawingCodeProcess
                // .setEndTime(DateTimeUtil.convertStringToTimestamp(drawingCodeProcessDto.getEndTime()));
                drawingCodeProcess.setIsPlan(1);
                return drawingCodeProcess;
        }

        public static DrawingCodeProcessDto mapToDrawingCodeProcessDto(DrawingCodeProcess drawingCodeProcess) {
                DrawingCodeProcessDto dto = new DrawingCodeProcessDto();
                dto.setProcessId(drawingCodeProcess.getProcessId());
                dto.setPartNumber(drawingCodeProcess.getPartNumber());
                dto.setStepNumber(drawingCodeProcess.getStepNumber());
                dto.setManufacturingPoint(drawingCodeProcess.getManufacturingPoint());
                dto.setPgTime(drawingCodeProcess.getPgTime());
                dto.setProcessType(drawingCodeProcess.getProcessType());
                dto.setProcessStatus(drawingCodeProcess.getProcessStatus());
                dto.setStatus(drawingCodeProcess.getStatus());
                if (drawingCodeProcess.getStartTime() == null) {
                        dto.setStartTime(null);
                } else {
                        dto.setStartTime(DateTimeUtil.convertTimestampToString(drawingCodeProcess.getStartTime()));
                        ;
                }
                if (drawingCodeProcess.getEndTime() == null) {
                        dto.setEndTime(null);
                } else {
                        dto.setEndTime(DateTimeUtil.convertTimestampToString(drawingCodeProcess.getEndTime()));
                }
                dto.setIsPlan(drawingCodeProcess.getIsPlan());
                dto.setOrderDetailId(drawingCodeProcess.getOrderDetail() != null
                                ? drawingCodeProcess.getOrderDetail().getOrderDetailId()
                                : null);
                dto.setMachineId(
                                drawingCodeProcess.getMachine() != null ? drawingCodeProcess.getMachine().getMachineId()
                                                : null);
                return dto;
        }

        public static DrawingCodeProcessResponseDto toDto(OrderDetailDto orderDetailDto, MachineDto machineDto,
                        DrawingCodeProcess drawingCodeProcess, List<StaffDto> staffDtos, PlanDto planDto,
                        ProcessTimeDto processTimeDto) {
                DrawingCodeProcessResponseDto dto = new DrawingCodeProcessResponseDto();
                dto.setProcessId(drawingCodeProcess.getProcessId());
                dto.setPartNumber(drawingCodeProcess.getPartNumber());
                dto.setStepNumber(drawingCodeProcess.getStepNumber());
                dto.setManufacturingPoint(drawingCodeProcess.getManufacturingPoint());
                dto.setProcessType(drawingCodeProcess.getProcessType());
                dto.setProcessStatus(drawingCodeProcess.getProcessStatus());
                dto.setPgTime(drawingCodeProcess.getPgTime());

                if (drawingCodeProcess.getStartTime() == null) {
                        dto.setStartTime(null);
                } else {
                        dto.setStartTime(DateTimeUtil.convertTimestampToString(drawingCodeProcess.getStartTime()));
                }
                if (drawingCodeProcess.getEndTime() == null) {
                        dto.setEndTime(null);
                } else {
                        dto.setEndTime(DateTimeUtil.convertTimestampToString(drawingCodeProcess.getEndTime()));
                }
                dto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(drawingCodeProcess.getCreatedDate()));
                dto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(drawingCodeProcess.getUpdatedDate()));
                dto.setIsPlan(drawingCodeProcess.getIsPlan());
                dto.setStatus(drawingCodeProcess.getStatus());
                if (machineDto != null) {
                        dto.setMachineDto(machineDto);
                }
                if (planDto != null) {
                        dto.setPlanDto(planDto);
                }
                if (processTimeDto != null) {
                        dto.setProcessTimeDto(processTimeDto);
                }
                if (staffDtos != null) {
                        dto.setStaffDtos(staffDtos);
                }
                dto.setOrderDetailDto(orderDetailDto);
                return dto;
        }

        // hello
        public static PlanDto mapToPlanDto(String processId,
                        DrawingCodeProcessResquestDto drawingCodeProcessResquestDto) {
                PlanDto plan = new PlanDto();
                plan.setProcessId(processId);
                plan.setInProgress(1);
                plan.setStartTime(drawingCodeProcessResquestDto.getStartTime());
                plan.setEndTime(drawingCodeProcessResquestDto.getEndTime());
                plan.setStatus(drawingCodeProcessResquestDto.getStatus());
                if (drawingCodeProcessResquestDto.getRemark() == null) {
                        plan.setRemark(drawingCodeProcessResquestDto.getRemark());
                } else {
                        plan.setRemark(null);
                }
                if (drawingCodeProcessResquestDto.getRemarkTime() == null) {
                        plan.setRemarkTime(null);
                } else {
                        plan.setRemarkTime(drawingCodeProcessResquestDto.getRemarkTime());
                }
                plan.setStaffId(drawingCodeProcessResquestDto.getStaffId());
                plan.setMachineId(drawingCodeProcessResquestDto.getMachineId());
                plan.setPlannerId(drawingCodeProcessResquestDto.getPlannerId());

                return plan;
        }
}
