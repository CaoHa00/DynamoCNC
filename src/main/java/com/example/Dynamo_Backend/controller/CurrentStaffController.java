package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.CurrentStaffDto;
import com.example.Dynamo_Backend.service.CurrentStaffService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/staff_machine")
public class CurrentStaffController {
    public final CurrentStaffService currentStaffService;

    @GetMapping
    public ResponseEntity<List<CurrentStaffDto>> getAllCurrentStaffs() {
        List<CurrentStaffDto> currentStaffs = currentStaffService.getAllCurrentStaffs();
        return ResponseEntity.status(HttpStatus.OK).body(currentStaffs);
    }

    @PostMapping
    public ResponseEntity<CurrentStaffDto> addCurrentStaff(@RequestBody CurrentStaffDto currentStaffRequestDto) {
        CurrentStaffDto currentStaff = currentStaffService.addCurrentStaff(currentStaffRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(currentStaff);

    }

    @PutMapping("/{currentStaff_id}")
    public ResponseEntity<CurrentStaffDto> updateCurrentStaff(@PathVariable("currentStaff_id") Long Id,
            @RequestBody CurrentStaffDto currentStaffDto) {
        CurrentStaffDto updatedCurrentStaff = currentStaffService.updateCurrentStaff(Id, currentStaffDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCurrentStaff);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentStaffs(@PathVariable("staff_machine_id") Long currentStaffId) {
        currentStaffService.deleteCurrentStaff(currentStaffId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{currentStaff_id}")
    public ResponseEntity<CurrentStaffDto> getCurrentStaffById(@PathVariable("currentStaff_id") Long Id) {
        CurrentStaffDto currentStaff = currentStaffService.getCurrentStaffById(Id);
        return ResponseEntity.ok(currentStaff);
    }

}
