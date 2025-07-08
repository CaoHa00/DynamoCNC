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

import com.example.Dynamo_Backend.dto.StaffDto;
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
    public ResponseEntity<StaffDto> addStaff(@RequestBody StaffDto staffDto) {
        StaffDto staff = staffService.addStaff(staffDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(staff);

    }

    @PutMapping("/{staff_id}")
    public ResponseEntity<StaffDto> updateStaff(@PathVariable("staff_id") String Id,
            @RequestBody StaffDto staffDto) {
        StaffDto updatedStaff = staffService.updateStaff(Id, staffDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedStaff);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStaffs(@RequestBody List<String> staffIds) {
        staffService.deleteStaff(staffIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{staff_id}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable("staff_id") String Id) {
        StaffDto staff = staffService.getStaffById(Id);
        return ResponseEntity.ok(staff);
    }

}
