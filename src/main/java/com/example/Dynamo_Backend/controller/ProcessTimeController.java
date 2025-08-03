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

import com.example.Dynamo_Backend.dto.ProcessTimeDto;
import com.example.Dynamo_Backend.service.ProcessTimeService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/process-time")
public class ProcessTimeController {
    public final ProcessTimeService processTimeService;

    @PostMapping
    public ResponseEntity<ProcessTimeDto> addProcessTime(@RequestBody ProcessTimeDto processTimeDto) {
        ProcessTimeDto newProcessTimeDto = processTimeService.addProcessTime(processTimeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProcessTimeDto);
    }

    @GetMapping
    public ResponseEntity<List<ProcessTimeDto>> getAllProcessTimes() {
        List<ProcessTimeDto> processTimeDtos = processTimeService.getAllProcessTime();
        return ResponseEntity.status(HttpStatus.OK).body(processTimeDtos);
    }

    @PostMapping("/{processTime_id}")
    public ResponseEntity<ProcessTimeDto> getProcessTimeById(@PathVariable("processTime_id") Integer Id) {
        ProcessTimeDto processTimeDto = processTimeService.getProcessTimeById(Id);
        return ResponseEntity.status(HttpStatus.CREATED).body(processTimeDto);
    }

    @PutMapping("/{processTime_id}")
    public ResponseEntity<ProcessTimeDto> updateProcessTime(@PathVariable("processTime_id") Integer Id,
            @RequestBody ProcessTimeDto processTimeDto) {
        ProcessTimeDto updatedProcessTimeDto = processTimeService.updateProcessTime(Id, processTimeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedProcessTimeDto);
    }

    @DeleteMapping("/{processTime_id}")
    public ResponseEntity<Void> deleteProcessTime(@PathVariable("processTime_id") Integer Id) {
        processTimeService.deleteProcessTime(Id);
        return ResponseEntity.ok().build();
    }
}
