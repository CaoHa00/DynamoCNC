package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.MachineDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;

public class DrawingCodeProcessMapper {
        public static DrawingCodeProcess mapToDrawingCodeProcess(DrawingCodeProcessDto drawingCodeProcessDto) {
                DrawingCodeProcess drawingCodeProcess = new DrawingCodeProcess();

                drawingCodeProcess.setProcessId(drawingCodeProcessDto.getProcessId());
                drawingCodeProcess.setPartNumber(drawingCodeProcessDto.getPartNumber());
                drawingCodeProcess.setStepNumber(drawingCodeProcessDto.getStepNumber());
                drawingCodeProcess.setManufacturingPoint(drawingCodeProcessDto.getManufacturingPoint());
                drawingCodeProcess.setPgTime(drawingCodeProcessDto.getPgTime());
                drawingCodeProcess.setStatus(drawingCodeProcessDto.getStatus());
                drawingCodeProcess.setOperateHistories(drawingCodeProcessDto.getOperatorHistories());
                drawingCodeProcess.setLogs(drawingCodeProcessDto.getStatstistics());
                drawingCodeProcess.setCreatedDate(0);
                drawingCodeProcess.setUpdatedDate(0);
                drawingCodeProcess.setQcNote(drawingCodeProcessDto.getQcNote());
                return drawingCodeProcess;
        }

        public static DrawingCodeProcessDto mapToDrawingCodeProcessDto(DrawingCodeProcess drawingCodeProcess) {
                String formattedCreatedDate = Instant.ofEpochMilli(drawingCodeProcess.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String formattedUpdatedDate = Instant.ofEpochMilli(drawingCodeProcess.getUpdatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return new DrawingCodeProcessDto(
                                drawingCodeProcess.getProcessId(),
                                drawingCodeProcess.getPartNumber(),
                                drawingCodeProcess.getStepNumber(),
                                drawingCodeProcess.getManufacturingPoint(),
                                drawingCodeProcess.getPgTime(),
                                drawingCodeProcess.getStartTime(),
                                drawingCodeProcess.getEndTime(),
                                formattedCreatedDate,
                                formattedUpdatedDate,
                                drawingCodeProcess.getStatus(),
                                drawingCodeProcess.getQcNote(),
                                drawingCodeProcess.getDrawingCode() != null
                                                ? drawingCodeProcess.getDrawingCode().getDrawingCodeId()
                                                : null,
                                drawingCodeProcess.getMachine() != null ? drawingCodeProcess.getMachine().getMachineId()
                                                : null,
                                drawingCodeProcess.getQuantityControl() != null
                                                ? drawingCodeProcess.getQuantityControl().getId()
                                                : null,
                                drawingCodeProcess.getOperateHistories(),
                                drawingCodeProcess.getLogs());
        }

        public static DrawingCodeProcessResponseDto toDto(DrawingCodeDto drawingCodeDto, MachineDto machineDto,
                        DrawingCodeProcess drawingCodeProcess) {
                DrawingCodeProcessResponseDto dto = new DrawingCodeProcessResponseDto();
                String formattedCreatedDate = Instant.ofEpochMilli(drawingCodeProcess.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String formattedUpdatedDate = Instant.ofEpochMilli(drawingCodeProcess.getCreatedDate())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dto.setProcessId(drawingCodeProcess.getProcessId());
                dto.setPartNumber(drawingCodeProcess.getPartNumber());
                dto.setStepNumber(drawingCodeProcess.getStepNumber());
                dto.setManufacturingPoint(drawingCodeProcess.getManufacturingPoint());
                dto.setPgTime(drawingCodeProcess.getPgTime());
                dto.setStartTime(drawingCodeProcess.getStartTime());
                dto.setEndTime(drawingCodeProcess.getEndTime());
                dto.setCreatedDate(formattedCreatedDate);
                dto.setUpdatedDate(formattedUpdatedDate);
                dto.setQcNote(drawingCodeProcess.getQcNote());
                dto.setStatus(drawingCodeProcess.getStatus());
                if (machineDto != null) {
                        dto.setMachineDto(machineDto);
                }
                dto.setDrawingCodeDto(drawingCodeDto);
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
