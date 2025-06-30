package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.entities.DrawingCode;

public class DrawingCodeMapper {
    public static DrawingCode mapToDrawingCode(DrawingCodeDto drawingCodeDto) {
        DrawingCode drawingCode = new DrawingCode();
        drawingCode.setDrawingCodeId(drawingCodeDto.getDrawingCodeId());
        drawingCode.setDrawingCodeName(drawingCodeDto.getDrawingCodeName());
        drawingCode.setCreatedDate(drawingCodeDto.getCreatedDate());
        drawingCode.setUpdatedDate(drawingCodeDto.getUpdatedDate());
        drawingCode.setStatus(drawingCodeDto.getStatus());
        return drawingCode;
        // return new DrawingCode(
        // drawingCodeDto.getDrawingCodeId(),
        // drawingCodeDto.getDrawingCodeName(),
        // drawingCodeDto.getOrders(),
        // drawingCodeDto.getDrawingCodeProcesses());
    }

    public static DrawingCodeDto mapToDrawingCodeDto(DrawingCode drawingCode) {
        DrawingCodeDto drawingCodeDto = new DrawingCodeDto();
        drawingCodeDto.setDrawingCodeId(drawingCode.getDrawingCodeId());
        drawingCodeDto.setDrawingCodeName(drawingCode.getDrawingCodeName());
        drawingCodeDto.setCreatedDate(drawingCode.getCreatedDate());
        drawingCodeDto.setUpdatedDate(drawingCode.getUpdatedDate());
        drawingCodeDto.setStatus(drawingCode.getStatus());
        return drawingCodeDto;
        // return new DrawingCodeDto(
        // drawingCode.getDrawingCodeId(),
        // drawingCode.getDrawingCodeName(),
        // drawingCode.getOrders(),
        // drawingCode.getDrawingCodeProcesses());
    }

}
