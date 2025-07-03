package com.example.Dynamo_Backend.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.entities.DrawingCode;

public class DrawingCodeMapper {
    public static DrawingCode mapToDrawingCode(DrawingCodeDto drawingCodeDto) {
        DrawingCode drawingCode = new DrawingCode();
        drawingCode.setDrawingCodeId(drawingCodeDto.getDrawingCodeId());
        drawingCode.setDrawingCodeName(drawingCodeDto.getDrawingCodeName());
        drawingCode.setCreatedDate(0);
        drawingCode.setUpdatedDate(0);
        drawingCode.setStatus(drawingCodeDto.getStatus());
        drawingCode.setProductStatus(drawingCodeDto.getProductStatus());
        return drawingCode;
        // return new DrawingCode(
        // drawingCodeDto.getDrawingCodeId(),
        // drawingCodeDto.getDrawingCodeName(),
        // drawingCodeDto.getOrders(),
        // drawingCodeDto.getDrawingCodeProcesses());
    }

    public static DrawingCodeDto mapToDrawingCodeDto(DrawingCode drawingCode) {
        DrawingCodeDto drawingCodeDto = new DrawingCodeDto();
        String formattedCreatedDate = Instant.ofEpochMilli(drawingCode.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedUpdatedDate = Instant.ofEpochMilli(drawingCode.getCreatedDate())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        drawingCodeDto.setDrawingCodeId(drawingCode.getDrawingCodeId());
        drawingCodeDto.setDrawingCodeName(drawingCode.getDrawingCodeName());
        drawingCodeDto.setCreatedDate(formattedCreatedDate);
        drawingCodeDto.setUpdatedDate(formattedUpdatedDate);
        drawingCodeDto.setStatus(drawingCode.getStatus());
        drawingCodeDto.setProductStatus(drawingCode.getProductStatus());
        return drawingCodeDto;
    }

}
