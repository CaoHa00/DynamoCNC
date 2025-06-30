package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Dynamo_Backend.dto.DrawingCodeDto;

import com.example.Dynamo_Backend.service.DrawingCodeService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/drawing-code")
public class DrawingCodeController {
    public final DrawingCodeService drawingCodeService;

    @GetMapping
    public ResponseEntity<List<DrawingCodeDto>> getAlldrawingCodes() {
        List<DrawingCodeDto> drawingCodes = drawingCodeService.getAllDrawingCode();
        return ResponseEntity.status(HttpStatus.OK).body(drawingCodes);
    }

    @PostMapping
    public ResponseEntity<DrawingCodeDto> adddrawingCode(@RequestBody DrawingCodeDto drawingCodeDto) {
        DrawingCodeDto drawingCode = drawingCodeService.addDrawingCode(drawingCodeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(drawingCode);

    }

    @PutMapping("/{drawingCode_id}")
    public ResponseEntity<DrawingCodeDto> updateDrawingCode(@PathVariable("drawingCode_id") String Id,
            @RequestBody DrawingCodeDto drawingCodeDto) {
        DrawingCodeDto updateDrawingCodes = drawingCodeService.updateDrawingCode(Id, drawingCodeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateDrawingCodes);
    }

    @DeleteMapping("/{drawingCode_id}")
    public ResponseEntity<Void> deletedrawingCode(@PathVariable("drawingCode_id") String Id) {
        drawingCodeService.deleteDrawingCode(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{drawingCode_id}")
    public ResponseEntity<DrawingCodeDto> getDrawingCodeById(@PathVariable("drawingCode_id") String Id) {
        DrawingCodeDto drawingCode = drawingCodeService.getDrawingCodeById(Id);
        return ResponseEntity.ok(drawingCode);
    }
}
