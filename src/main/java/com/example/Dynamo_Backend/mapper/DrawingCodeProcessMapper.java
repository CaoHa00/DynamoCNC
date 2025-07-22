package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.OrderDetailDto;
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
                // drawingCodeProcess.setPgRunTime(drawingCodeProcessDto.getPgRunTime());
                // drawingCodeProcess.setOffsetRunTime(drawingCodeProcessDto.getOffsetRunTime());
                // drawingCodeProcess.setTotalRunningTime(drawingCodeProcessDto.getTotalRunningTime());
                // drawingCodeProcess.setTotalStopTime(drawingCodeProcessDto.getTotalStopTime());
                drawingCodeProcess.setStatus(drawingCodeProcessDto.getStatus());
                drawingCodeProcess.setOperateHistories(drawingCodeProcessDto.getStaffHistories());
                drawingCodeProcess.setLogs(drawingCodeProcessDto.getStatstistics());
                drawingCodeProcess.setCreatedDate(0);
                drawingCodeProcess.setUpdatedDate(0);
                drawingCodeProcess.setIsPlan(drawingCodeProcessDto.getIsPlan());
                return drawingCodeProcess;
        }

        public static DrawingCodeProcessDto mapToDrawingCodeProcessDto(DrawingCodeProcess drawingCodeProcess) {
                return new DrawingCodeProcessDto(
                                drawingCodeProcess.getProcessId(),
                                drawingCodeProcess.getPartNumber(),
                                drawingCodeProcess.getStepNumber(),
                                drawingCodeProcess.getManufacturingPoint(),
                                // drawingCodeProcess.getOffsetRunTime(),
                                // drawingCodeProcess.getTotalStopTime(),
                                // drawingCodeProcess.getOffsetRunTime(),
                                // drawingCodeProcess.getPgRunTime(),
                                drawingCodeProcess.getStartTime(),
                                drawingCodeProcess.getEndTime(),
                                DateTimeUtil.convertTimestampToStringDate(drawingCodeProcess.getCreatedDate()),
                                DateTimeUtil.convertTimestampToStringDate(drawingCodeProcess.getUpdatedDate()),
                                drawingCodeProcess.getStatus(),
                                drawingCodeProcess.getIsPlan(),
                                drawingCodeProcess.getOrderDetail() != null
                                                ? drawingCodeProcess.getOrderDetail().getOrderDetailId()
                                                : null,
                                drawingCodeProcess.getMachine() != null ? drawingCodeProcess.getMachine().getMachineId()
                                                : null,
                                drawingCodeProcess.getOperateHistories(),
                                drawingCodeProcess.getLogs());
        }

        public static DrawingCodeProcessResponseDto toDto(OrderDetailDto orderDetailDto, MachineDto machineDto,
                        DrawingCodeProcess drawingCodeProcess) {
                DrawingCodeProcessResponseDto dto = new DrawingCodeProcessResponseDto();
                dto.setProcessId(drawingCodeProcess.getProcessId());
                dto.setPartNumber(drawingCodeProcess.getPartNumber());
                dto.setStepNumber(drawingCodeProcess.getStepNumber());
                dto.setManufacturingPoint(drawingCodeProcess.getManufacturingPoint());
                // dto.setPgRunTime(drawingCodeProcess.getPgRunTime());
                // dto.setOffsetRunTime(drawingCodeProcess.getOffsetRunTime());
                // dto.setTotalRunningTime(drawingCodeProcess.getTotalRunningTime());
                // dto.setTotalStopTime(drawingCodeProcess.getTotalStopTime());
                dto.setStartTime(drawingCodeProcess.getStartTime());
                dto.setEndTime(drawingCodeProcess.getEndTime());
                dto.setCreatedDate((DateTimeUtil.convertTimestampToStringDate(drawingCodeProcess.getCreatedDate())));
                dto.setUpdatedDate((DateTimeUtil.convertTimestampToStringDate(drawingCodeProcess.getUpdatedDate())));
                dto.setIsPlan(drawingCodeProcess.getIsPlan());
                dto.setStatus(drawingCodeProcess.getStatus());
                if (machineDto != null) {
                        dto.setMachineDto(machineDto);
                }
                dto.setOrderDetailDto(orderDetailDto);
                return dto;

        }

        // public static DrawingCodeProcessResquestDto toEnity(DrawingCodeProcessDto
        // drawingCodeProcessDto) {
        // DrawingCodeProcessResquestDto entity = new DrawingCodeProcessResquestDto();
        // entity.setProcessId(drawingCodeProcessDto.getProcessId());
        // entity.setDrawingCodeId(drawingCodeProcessDto.getDrawingCodeId());
        // entity.setPartNumber(drawingCodeProcessDto.getPartNumber());
        // entity.setStepNumber(drawingCodeProcessDto.getStepNumber());
        // entity.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
        // entity.setPgTime(drawingCodeProcessDto.getPgTime());
        // entity.setStartTime(drawingCodeProcessDto.getStartTime());
        // entity.setEndTime(drawingCodeProcessDto.getEndTime());
        // entity.setAddDate(drawingCodeProcessDto.getAddDate());
        // entity.setStatus(drawingCodeProcessDto.getStatus());
        // entity.setDrawingCodeId(drawingCodeProcessDto.getDrawingCodeId());
        // entity.setMachineId(drawingCodeProcessDto.getMachineId());
        // return entity;

        // }
}
