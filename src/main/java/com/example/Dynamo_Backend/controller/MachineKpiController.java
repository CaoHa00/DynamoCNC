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

import com.example.Dynamo_Backend.dto.MachineKpiDto;

import com.example.Dynamo_Backend.service.MachineKpiService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/machine-kpi")
public class MachineKpiController {

    public final MachineKpiService machineKpiService;

    @GetMapping
    public ResponseEntity<List<MachineKpiDto>> getAllMachineKpis() {
        List<MachineKpiDto> machineKpis = machineKpiService.getMachineKpis();
        return ResponseEntity.status(HttpStatus.OK).body(machineKpis);
    }

    @PostMapping
    public ResponseEntity<MachineKpiDto> addMachineKpi(@RequestBody MachineKpiDto machineKpiDto) {
        MachineKpiDto machineKpi = machineKpiService.addMachineKpi(machineKpiDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(machineKpi);

    }

    @PutMapping("/{machineKpi_id}")
    public ResponseEntity<MachineKpiDto> updateMachineKpi(@PathVariable("machineKpi_id") Integer Id,
            @RequestBody MachineKpiDto machineKpiDto) {
        MachineKpiDto updateMachineKpis = machineKpiService.updateMachineKpi(Id, machineKpiDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateMachineKpis);
    }

    @DeleteMapping("/{machineKpi_id}")
    public ResponseEntity<Void> deleteMachineKpi(@PathVariable("machineKpi_id") Integer Id) {
        machineKpiService.deleteMachineKpi(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{machineKpi_id}")
    public ResponseEntity<MachineKpiDto> getMachineKpiById(@PathVariable("machineKpi_id") Integer Id) {
        MachineKpiDto machineKpi = machineKpiService.getMachineKpiById(Id);
        return ResponseEntity.ok(machineKpi);
    }
}
