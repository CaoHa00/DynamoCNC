package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Dynamo_Backend.dto.StaffDto;
import com.example.Dynamo_Backend.dto.RequestDto.StaffRequestDto;
import com.example.Dynamo_Backend.service.StaffService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/staff")
public class StaffController {
    public final StaffService staffService;

    @GetMapping
    public ResponseEntity<List<StaffDto>> getAllStaffs() {
        List<StaffDto> staffs = staffService.getAllStaffs();
        return ResponseEntity.status(HttpStatus.OK).body(staffs);
    }

    @PostMapping
    public ResponseEntity<StaffDto> addStaff(@RequestBody StaffRequestDto staffRequestDto) {
        StaffDto staff = staffService.addStaff(staffRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(staff);

    }

    @PutMapping("/{staff_id}")
    public ResponseEntity<StaffDto> updateStaff(@PathVariable("staff_id") String Id,
            @RequestBody StaffDto staffDto) {
        StaffDto updatedStaff = staffService.updateStaff(Id, staffDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedStaff);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStaffs(@RequestParam List<String> staffIds) {
        staffService.deleteStaff(staffIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{staff_id}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable("staff_id") String Id) {
        StaffDto staff = staffService.getStaffById(Id);
        return ResponseEntity.ok(staff);
    }

}
