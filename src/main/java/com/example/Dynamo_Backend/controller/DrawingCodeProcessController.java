package com.example.Dynamo_Backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
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
        List<DrawingCodeProcessResponseDto> drawingCodes = drawingCodeProcessService.getAllTodoProcesses();
        return ResponseEntity.status(HttpStatus.OK).body(drawingCodes);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DrawingCodeProcessResponseDto>> getAll() {
        List<DrawingCodeProcessResponseDto> drawingCodes = drawingCodeProcessService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(drawingCodes);
    }

    @GetMapping("/planned")
    public ResponseEntity<Page<DrawingCodeProcessResponseDto>> getAllPlannedDrawingCodes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DrawingCodeProcessResponseDto> drawingCodes = drawingCodeProcessService.getPlannedProcesses(1, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(drawingCodes);
    }

    @GetMapping("/unplanned")
    public ResponseEntity<Page<DrawingCodeProcessResponseDto>> getAllUnplannedDrawingCodes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DrawingCodeProcessResponseDto> drawingCodes = drawingCodeProcessService.getPlannedProcesses(0, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(drawingCodes);
    }

    @GetMapping("/orderDetail/{orderDetailId}")
    public ResponseEntity<List<DrawingCodeProcessDto>> getDrawingCodesByOrderDetail(
            @PathVariable("orderDetailId") String orderDetailId) {
        List<DrawingCodeProcessDto> drawingCodes = drawingCodeProcessService.getProcessesByOrderDetail(orderDetailId);
        return ResponseEntity.status(HttpStatus.OK).body(drawingCodes);
    }

    @PostMapping
    public ResponseEntity<DrawingCodeProcessDto> adddrawingCodeProcess(
            @RequestBody DrawingCodeProcessResquestDto drawingCodeProcessDto) {
        DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService
                .addDrawingCodeProcess(drawingCodeProcessDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(drawingCodeProcess);
    }

    @PostMapping("/by-operator")
    public ResponseEntity<DrawingCodeProcessDto> addProcessByOperator(
            @RequestBody DrawingCodeProcessResquestDto drawingCodeProcessDto) {
        DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService
                .addProcessByOperator(drawingCodeProcessDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(drawingCodeProcess);
    }

    @PostMapping("/done-process/{process_id}")
    public ResponseEntity<Void> doneProcess(@PathVariable("process_id") String processId) {
        drawingCodeProcessService.doneProcess(processId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/receive")
    public ResponseEntity<Void> receiveDataFromTablet(@RequestParam("drawingCodeProcess_id") String Id,
            @RequestParam("staffId") String staffId, @RequestParam("machineId") Integer machineId) {
        drawingCodeProcessService.receiveProcessFromTablet(Id, machineId, staffId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/tablet/{drawingCodeProcess_id}")
    public ResponseEntity<DrawingCodeProcessResponseDto> updateDataFromTablet(
            @PathVariable("drawingCodeProcess_id") String Id,
            @RequestBody DrawingCodeProcessResquestDto drawingCodeProcessDto) {
        DrawingCodeProcessResponseDto updateDrawingCodeProcesses = drawingCodeProcessService.updateProcessByOperator(
                Id,
                drawingCodeProcessDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateDrawingCodeProcesses);
    }

    @PutMapping("/update")
    public ResponseEntity<DrawingCodeProcessDto> updateMachine(
            @RequestBody DrawingCodeProcessResquestDto drawingCodeProcessDto) {
        DrawingCodeProcessDto updateDrawingCodeProcesses = drawingCodeProcessService
                .updateDrawingCodeProcess(drawingCodeProcessDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateDrawingCodeProcesses);
    }

    @PutMapping("/{drawingCodeProcess_id}")
    public ResponseEntity<DrawingCodeProcessResponseDto> updateDrawingCodeProcess(
            @PathVariable("drawingCodeProcess_id") String Id,
            @RequestBody DrawingCodeProcessResquestDto drawingCodeProcessDto) {
        DrawingCodeProcessResponseDto updateDrawingCodeProcesses = drawingCodeProcessService.updateDrawingCodeProcess(
                Id,
                drawingCodeProcessDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateDrawingCodeProcesses);
    }

    @PutMapping("/admin/{process_id}")
    public ResponseEntity<DrawingCodeProcessResponseDto> updateDrawingCodeProcessByAdmin(
            @PathVariable("process_id") String Id,
            @RequestBody DrawingCodeProcessResquestDto drawingCodeProcessDto) {
        DrawingCodeProcessResponseDto updateDrawingCodeProcesses = drawingCodeProcessService.updateProcessByAdmin(
                Id,
                drawingCodeProcessDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateDrawingCodeProcesses);
    }

    @DeleteMapping("/{drawingCodeProcess_id}")
    public ResponseEntity<Void> deletedrawingCodeProcess(@PathVariable("drawingCodeProcess_id") String Id) {
        drawingCodeProcessService.deleteDrawingCodeProcess(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/id/{drawingCodeProcess_id}")
    public ResponseEntity<DrawingCodeProcessDto> getDrawingCodeProcessById(
            @PathVariable("drawingCodeProcess_id") String Id) {
        DrawingCodeProcessDto drawingCodeProcess = drawingCodeProcessService.getDrawingCodeProcessById(Id);
        return ResponseEntity.ok(drawingCodeProcess);
    }

    @GetMapping("machine/{machineId}")
    public ResponseEntity<Map<String, Object>> getDrawingCodeProcessByMachineId(
            @PathVariable("machineId") Integer Id) {
        Map<String, Object> drawingCodeProcess = drawingCodeProcessService
                .getDrawingCodeProcessByMachineId(Id);
        return ResponseEntity.ok(drawingCodeProcess);
    }

    @GetMapping("/staff")
    public ResponseEntity<List<DrawingCodeProcessResponseDto>> getProcessesByStaffAndTime(
            @RequestParam(name = "staff_id", required = false) String staffId,
            @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long stop) {

        if (start != null && start == 0)
            start = null;
        if (stop != null && stop == 0)
            stop = null;
        List<DrawingCodeProcessResponseDto> drawingCodeProcess = drawingCodeProcessService.getProcessesByOperator(
                staffId,
                start, stop);
        return ResponseEntity.ok(drawingCodeProcess);
    }

    @GetMapping("/machine")
    public ResponseEntity<List<DrawingCodeProcessResponseDto>> getProcessesByMachineAndTime(
            @RequestParam(name = "machine_id", required = false) Integer machineId,
            @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long stop) {
        if (start != null && start == 0)
            start = null;
        if (stop != null && stop == 0)
            stop = null;
        List<DrawingCodeProcessResponseDto> drawingCodeProcess = drawingCodeProcessService.getProcessByMachine(
                machineId,
                start, stop);
        return ResponseEntity.ok(drawingCodeProcess);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<DrawingCodeProcessResponseDto>> getCompletedProcess(
            @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long stop) {
        if (start != null && start == 0)
            start = null;
        if (stop != null && stop == 0)
            stop = null;
        List<DrawingCodeProcessResponseDto> processes = drawingCodeProcessService.getCompletedProcess(3, start,
                stop);
        return ResponseEntity.status(HttpStatus.OK).body(processes);
    }

    @GetMapping("/completed-with-history")
    public ResponseEntity<List<DrawingCodeProcessResponseDto>> getCompletedProcessWithOperateHistoryData(
            @RequestParam(name = "staff_id", required = false) String staffId,
            @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long stop) {
        if (start != null && start == 0)
            start = null;
        if (stop != null && stop == 0)
            stop = null;
        List<DrawingCodeProcessResponseDto> processes = drawingCodeProcessService
                .getCompletedProcessWithOperateHistoryData(staffId, start,
                        stop);
        return ResponseEntity.status(HttpStatus.OK).body(processes);
    }

}
