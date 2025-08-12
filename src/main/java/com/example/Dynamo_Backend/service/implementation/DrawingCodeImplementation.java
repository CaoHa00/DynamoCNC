package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;
import com.example.Dynamo_Backend.entities.DrawingCode;
import com.example.Dynamo_Backend.mapper.DrawingCodeMapper;
import com.example.Dynamo_Backend.repository.DrawingCodeRepository;
import com.example.Dynamo_Backend.repository.OrderDetailRepository;
import com.example.Dynamo_Backend.service.DrawingCodeService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;

@AllArgsConstructor
@Service
public class DrawingCodeImplementation implements DrawingCodeService {
    DrawingCodeRepository drawingCodeRepository;

    @Override
    public DrawingCodeDto addDrawingCode(DrawingCodeDto drawingCodeDto) {
        long createdTimestamp = System.currentTimeMillis();
        int status = 1;

        DrawingCode drawingCode = DrawingCodeMapper.mapToDrawingCode(drawingCodeDto);
        drawingCode.setCreatedDate(createdTimestamp);
        drawingCode.setUpdatedDate(createdTimestamp);
        drawingCode.setStatus(status);
        // drawingCode.setProductStatus("status");
        DrawingCode saveDrawingCode = drawingCodeRepository.save(drawingCode);
        return DrawingCodeMapper.mapToDrawingCodeDto(saveDrawingCode);
    }

    @Override
    public DrawingCodeDto updateDrawingCode(String drawingCodeId, DrawingCodeDto drawingCodeDto) {
        DrawingCode drawingCode = drawingCodeRepository.findById(drawingCodeId)
                .orElseThrow(() -> new RuntimeException("DrawingCode is not found:" + drawingCodeId));
        long updatedTimestamp = System.currentTimeMillis();
        drawingCode.setDrawingCodeName(drawingCodeDto.getDrawingCodeName());
        drawingCode.setStatus(drawingCodeDto.getStatus());
        drawingCode.setUpdatedDate(updatedTimestamp);
        // drawingCode.setOrders(drawingCode.getOrders());
        // drawingCode.setDrawingCodeProcesses(drawingCodeDto.getDrawingCodeProcesses());
        DrawingCode updatedDrawingCode = drawingCodeRepository.save(drawingCode);
        // orderDetailService.updateOrderCode(drawingCodeId, "");
        return DrawingCodeMapper.mapToDrawingCodeDto(updatedDrawingCode);
    }

    @Override
    public DrawingCodeDto getDrawingCodeById(String drawingCodeId) {
        DrawingCode drawingCode = drawingCodeRepository.findById(drawingCodeId)
                .orElseThrow(() -> new RuntimeException("DrawingCode is not found:" + drawingCodeId));
        return DrawingCodeMapper.mapToDrawingCodeDto(drawingCode);
    }

    @Override
    public void deleteDrawingCode(String drawingCodeId) {
        DrawingCode drawingCode = drawingCodeRepository.findById(drawingCodeId)
                .orElseThrow(() -> new RuntimeException("DrawingCode is not found:" + drawingCodeId));
        drawingCodeRepository.delete(drawingCode);
    }

    @Override
    public List<DrawingCodeDto> getAllDrawingCode() {
        List<DrawingCode> drawingCodes = drawingCodeRepository.findAll();
        return drawingCodes.stream().map(DrawingCodeMapper::mapToDrawingCodeDto).toList();
    }

    @Override
    public void importDrawingCodeFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = ((MultipartFile) file).getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<DrawingCode> drawingCodes = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;
                DrawingCode drawingCode = new DrawingCode();
                drawingCode.setDrawingCodeName(row.getCell(0).getStringCellValue());
                drawingCode.setStatus(1);
                long createdTimestamp = System.currentTimeMillis();
                drawingCode.setCreatedDate(createdTimestamp);
                drawingCode.setUpdatedDate(createdTimestamp);
                drawingCodes.add(drawingCode);
            }
            drawingCodeRepository.saveAll(drawingCodes);
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to import staff from Excel file: " + e.getMessage());
        }
    }

    @Override
    public List<DrawingCodeDto> getAllActiveDrawingCode() {
        List<DrawingCode> drawingCodes = drawingCodeRepository.findAllByStatus(1);
        return drawingCodes.stream().map(DrawingCodeMapper::mapToDrawingCodeDto).toList();
    }
}
