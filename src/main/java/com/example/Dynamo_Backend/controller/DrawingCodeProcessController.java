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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Dynamo_Backend.dto.DrawingCodeProcessDto;
import com.example.Dynamo_Backend.dto.RequestDto.DrawingCodeProcessResquestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.DrawingCodeProcessResponseDto;
import com.example.Dynamo_Backend.service.DrawingCodeProcessService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/drawing-code-process")
public class DrawingCodeProcessController {
    public final DrawingCodeProcessService drawingCodeProcessService;

    @GetMapping
    public ResponseEntity<List<DrawingCodeProcessResponseDto>> getAlldrawingCodes() {
        List<DrawingCodeProcessResponseDto> drawingCodes = drawingCodeProcessService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(drawingCodes);
    }

    @PostMapping
    public ResponseEntity<DrawingCodeProcessDto> adddrawingCodeProcess(
            @RequestBody DrawingCodeProcessResquestDto drawingCodeProcessDto) {
        DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService
                .addDrawingCodeProcess(drawingCodeProcessDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(drawingCodeProcess);

    }

    @PostMapping("/receive")
    public ResponseEntity<Void> receiveDataFromTablet(@RequestParam("drawingCodeProcess_id") String Id,
            @RequestParam("staffId") String staffId, @RequestParam("machineId") Integer machineId) {
        drawingCodeProcessService.receiveProcessFromTablet(Id, machineId, staffId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{drawingCodeProcess_id}")
    public ResponseEntity<DrawingCodeProcessResponseDto> updateDrawingCodeProcess(
            @PathVariable("drawingCodeProcess_id") String Id,
            @RequestBody DrawingCodeProcessDto drawingCodeProcessDto) {
        DrawingCodeProcessResponseDto updateDrawingCodeProcesses = drawingCodeProcessService.updateDrawingCodeProcess(
                Id,
                drawingCodeProcessDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateDrawingCodeProcesses);
    }

    @DeleteMapping("/{drawingCodeProcess_id}")
    public ResponseEntity<Void> deletedrawingCodeProcess(@PathVariable("drawingCodeProcess_id") String Id) {
        drawingCodeProcessService.deleteDrawingCodeProcess(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{drawingCodeProcess_id}")
    public ResponseEntity<DrawingCodeProcessDto> getDrawingCodeProcessById(
            @PathVariable("drawingCodeProcess_id") String Id) {
        DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService.getDrawingCodeProcessById(Id);
        return ResponseEntity.ok(drawingCodeProcess);
    }

    @GetMapping("machine/{machineId}")
    public ResponseEntity<DrawingCodeProcessDto> getDrawingCodeProcessByMachineId(
            @PathVariable("machineId") Integer Id) {
        DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService.getDrawingCodeProcessByMachineId(Id);
        return ResponseEntity.ok(drawingCodeProcess);
    }

}
