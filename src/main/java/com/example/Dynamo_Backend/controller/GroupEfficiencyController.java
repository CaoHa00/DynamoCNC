package com.example.Dynamo_Backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.RequestDto.GroupEfficiencyRequestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.GroupEfficiencyResponseDto;
import com.example.Dynamo_Backend.service.GroupEfficiencyService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/group-efficiency")
public class GroupEfficiencyController {

    private final GroupEfficiencyService groupEfficiencyService;

    @GetMapping
    public ResponseEntity<GroupEfficiencyResponseDto> getGroupEfficiency(
            @RequestBody GroupEfficiencyRequestDto requestDto) {
        GroupEfficiencyResponseDto efficiency = groupEfficiencyService.getGroupEfficiency(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(efficiency);
    }

}
