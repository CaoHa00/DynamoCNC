package com.example.Dynamo_Backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;

public interface DrawingCodeService {
    DrawingCodeDto addDrawingCode(DrawingCodeDto drawingCodeDto);

    DrawingCodeDto updateDrawingCode(String drawingCodeId, DrawingCodeDto drawingCodeDto);

    DrawingCodeDto getDrawingCodeById(String drawingCodeId);

    void deleteDrawingCode(String drawingCodeId);

    List<DrawingCodeDto> getAllDrawingCode();

    void importDrawingCodeFromExcel(MultipartFile file);
}
