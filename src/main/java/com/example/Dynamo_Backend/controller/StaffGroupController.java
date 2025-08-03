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

import com.example.Dynamo_Backend.dto.StaffGroupDto;
import com.example.Dynamo_Backend.service.StaffGroupService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/staff-group")
public class StaffGroupController {
    public final StaffGroupService staffGroupService;

    @GetMapping
    public ResponseEntity<List<StaffGroupDto>> getAllStaffGroups() {
        List<StaffGroupDto> staffGroups = staffGroupService.getStaffGroups();
        return ResponseEntity.status(HttpStatus.OK).body(staffGroups);
    }

    @PostMapping
    public ResponseEntity<StaffGroupDto> addStaffGroup(@RequestBody StaffGroupDto staffGroupDto) {
        StaffGroupDto staffGroup = staffGroupService.addStaffGroup(staffGroupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(staffGroup);

    }

    @PutMapping("/{staffGroup_id}")
    public ResponseEntity<StaffGroupDto> updateStaffGroup(@PathVariable("staffGroup_id") String Id,
            @RequestBody StaffGroupDto staffGroupDto) {
        StaffGroupDto updatedStaffGroup = staffGroupService.updateStaffGroup(Id, staffGroupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedStaffGroup);
    }

    @DeleteMapping("/{staffGroup_id}")
    public ResponseEntity<Void> deleteStaffGroup(@PathVariable("staffGroup_id") String Id) {
        staffGroupService.deleteStaffGroup(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{staffGroup_id}")
    public ResponseEntity<StaffGroupDto> getStaffGroupById(@PathVariable("staffGroup_id") String Id) {
        StaffGroupDto staffGroup = staffGroupService.getStaffGroupById(Id);
        return ResponseEntity.ok(staffGroup);
    }
}
