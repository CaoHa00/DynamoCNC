package com.example.Dynamo_Backend.mapper;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.entities.DrawingCode;
import com.example.Dynamo_Backend.util.DateTimeUtil;

public class DrawingCodeMapper {
    public static DrawingCode mapToDrawingCode(DrawingCodeDto drawingCodeDto) {
        DrawingCode drawingCode = new DrawingCode();
        drawingCode.setDrawingCodeId(drawingCodeDto.getDrawingCodeId());
        drawingCode.setDrawingCodeName(drawingCodeDto.getDrawingCodeName());
        drawingCode.setCreatedDate(0);
        drawingCode.setUpdatedDate(0);
        // drawingCode.setStatus(drawingCodeDto.getStatus());
        // drawingCode.setProductStatus(drawingCodeDto.getProductStatus());
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
        drawingCodeDto.setCreatedDate(DateTimeUtil.convertTimestampToStringDate(drawingCode.getCreatedDate()));
        drawingCodeDto.setUpdatedDate(DateTimeUtil.convertTimestampToStringDate(drawingCode.getUpdatedDate()));
        // drawingCodeDto.setStatus(drawingCode.getStatus());
        // drawingCodeDto.setProductStatus(drawingCode.getProductStatus());
        return drawingCodeDto;
    }

}
