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

import com.example.Dynamo_Backend.dto.GroupKpiDto;
import com.example.Dynamo_Backend.service.GroupKpiService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/groupKpi")
public class GroupKpiController {
    public final GroupKpiService groupKpiService;

    @GetMapping
    public ResponseEntity<List<GroupKpiDto>> getAllGroupKpis() {
        List<GroupKpiDto> groupKpis = groupKpiService.getGroupKpis();
        return ResponseEntity.status(HttpStatus.OK).body(groupKpis);
    }

    @PostMapping
    public ResponseEntity<GroupKpiDto> addGroupKpi(@RequestBody GroupKpiDto groupKpiDto) {
        GroupKpiDto groupKpi = groupKpiService.addGroupKpi(groupKpiDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupKpi);

    }

    @PutMapping("/{groupKpi_id}")
    public ResponseEntity<GroupKpiDto> updateGroupKpi(@PathVariable("groupKpi_id") Integer Id,
            @RequestBody GroupKpiDto groupKpiDto) {
        GroupKpiDto updateGroupKpis = groupKpiService.updateGroupKpi(Id, groupKpiDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateGroupKpis);
    }

    @DeleteMapping("/{groupKpi_id}")
    public ResponseEntity<Void> deleteGroupKpi(@PathVariable("groupKpi_id") Integer Id) {
        groupKpiService.deleteGroupKpi(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupKpi_id}")
    public ResponseEntity<GroupKpiDto> getGroupKpiById(@PathVariable("groupKpi_id") Integer Id) {
        GroupKpiDto groupKpi = groupKpiService.getGroupKpiById(Id);
        return ResponseEntity.ok(groupKpi);
    }

    @PostMapping("/upload/week")
    public ResponseEntity<Void> uploadWeeklyGroupKpiExcel(@RequestParam("file") MultipartFile file) {
        groupKpiService.importGroupKpiWeekFromExcel(file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload/month")
    public ResponseEntity<Void> uploadMonthlyGroupKpiExcel(@RequestParam("file") MultipartFile file) {
        groupKpiService.importGroupKpiMonthFromExcel(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/currentWeek")
    public ResponseEntity<List<GroupKpiDto>> getGroupKpiByCurrentWeek() {
        List<GroupKpiDto> groupKpis = groupKpiService.getGroupKpiByCurrentWeek();
        return ResponseEntity.status(HttpStatus.OK).body(groupKpis);
    }

}
