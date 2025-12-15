package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.CurrentStatusDto;
import com.example.Dynamo_Backend.dto.ResponseDto.CurrentStatusResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.ListCurrentStaffStatusDto;
import com.example.Dynamo_Backend.service.CurrentStatusService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/current-status")
public class CurrentStatusController {
    public final CurrentStatusService currentStatusService;

    @GetMapping
    public ResponseEntity<List<CurrentStatusDto>> getAllCurrentStatuss() {
        List<CurrentStatusDto> currentStatuss = currentStatusService.getAllCurrentStatus();
        return ResponseEntity.status(HttpStatus.OK).body(currentStatuss);
    }

    @PostMapping
    public ResponseEntity<CurrentStatusDto> addCurrentStatus(
            @RequestBody CurrentStatusDto currentStatusDto) {
        CurrentStatusDto currentStatus = currentStatusService.addCurrentStatus(currentStatusDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(currentStatus);

    }

    @PutMapping("/{currentStatus_id}")
    public ResponseEntity<CurrentStatusDto> updateCurrentStatus(@PathVariable("currentStatus_id") String Id,
            @RequestBody CurrentStatusDto currentStatusDto) {
        CurrentStatusDto updatedCurrentStatus = currentStatusService.updateCurrentStatus(Id, currentStatusDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCurrentStatus);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentStatuss(@RequestParam String currentStatusId) {
        currentStatusService.deleteCurrentStatus(currentStatusId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{currentStatus_id}")
    public ResponseEntity<CurrentStatusDto> getCurrentStatusById(@PathVariable("currentStatus_id") String Id) {
        CurrentStatusDto currentStatus = currentStatusService.getCurrentStatusById(Id);
        return ResponseEntity.ok(currentStatus);
    }

    @GetMapping("/by-group/{groupId}")
    public ResponseEntity<List<CurrentStatusResponseDto>> getCurrentStatusByGroupId(
            @PathVariable("groupId") String groupId) {
        List<CurrentStatusResponseDto> currentStatuss = currentStatusService.getCurrentStatusByGroupId(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(currentStatuss);
    }

    @GetMapping("/staff/by-group/{groupId}")
    public ResponseEntity<List<ListCurrentStaffStatusDto>> getCurrentStaffStatusByGroupId(
            @PathVariable("groupId") String groupId) {
        List<ListCurrentStaffStatusDto> currentStatuss = currentStatusService.getCurrentStaffStatusByGroupId(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(currentStatuss);
    }

}
