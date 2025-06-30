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

import com.example.Dynamo_Backend.dto.MachineDto;

import com.example.Dynamo_Backend.service.MachineService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/machine")
public class MachineController {

    public final MachineService machineService;

    @GetMapping
    public ResponseEntity<List<MachineDto>> getAllMachines() {
        List<MachineDto> machines = machineService.getMachines();
        return ResponseEntity.status(HttpStatus.OK).body(machines);
    }

    @PostMapping
    public ResponseEntity<MachineDto> addMachine(@RequestBody MachineDto machineDto) {
        MachineDto machine = machineService.addMachine(machineDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(machine);

    }

    @PutMapping("/{machine_id}")
    public ResponseEntity<MachineDto> updateMachine(@PathVariable("machine_id") Integer Id,
            @RequestBody MachineDto machineDto) {
        MachineDto updateMachines = machineService.updateMachine(Id, machineDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateMachines);
    }

    @DeleteMapping("/{machine_id}")
    public ResponseEntity<Void> deleteMachine(@PathVariable("machine_id") Integer Id) {
        machineService.deleteMachine(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{machine_id}")
    public ResponseEntity<MachineDto> getMachineById(@PathVariable("machine_id") Integer Id) {
        MachineDto machine = machineService.getMachineById(Id);
        return ResponseEntity.ok(machine);
    }
}
