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
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.StaffKpiDto;

import com.example.Dynamo_Backend.service.StaffKpiService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/staff-kpi")
public class StaffKpiController {

    public final StaffKpiService staffKpiService;

    @GetMapping
    public ResponseEntity<List<StaffKpiDto>> getAllStaffKpis() {
        List<StaffKpiDto> staffKpis = staffKpiService.getStaffKpis();
        return ResponseEntity.status(HttpStatus.OK).body(staffKpis);
    }

    @PostMapping
    public ResponseEntity<StaffKpiDto> addStaffKpi(@RequestBody StaffKpiDto staffKpiDto) {
        StaffKpiDto staffKpi = staffKpiService.addStaffKpi(staffKpiDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(staffKpi);

    }

    @PutMapping("/{staff_id}")
    public ResponseEntity<StaffKpiDto> updateStaffKpi(@PathVariable("staff_id") Integer staffId,
            @RequestBody StaffKpiDto staffKpiDto) {
        StaffKpiDto updateStaffKpis = staffKpiService.updateStaffKpi(staffId, staffKpiDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateStaffKpis);
    }

    @DeleteMapping("/{staffKpi_id}")
    public ResponseEntity<Void> deleteStaffKpi(@PathVariable("staffKpi_id") Integer Id) {
        staffKpiService.deleteStaffKpi(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{staffKpi_id}")
    public ResponseEntity<StaffKpiDto> getStaffKpiById(@PathVariable("staffKpi_id") Integer Id) {
        StaffKpiDto staffKpi = staffKpiService.getStaffKpiById(Id);
        return ResponseEntity.ok(staffKpi);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadStaffKpiExcel(@RequestParam("file") MultipartFile file) {
        staffKpiService.importStaffKpiFromExcel(file);
        return ResponseEntity.ok().build();
    }
}
