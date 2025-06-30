package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.Dynamo_Backend.dto.MachineGroupDto;
import com.example.Dynamo_Backend.service.MachineGroupService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/machine-group")
@RestController
public class MachineGroupController {
    @Autowired
    MachineGroupService machineGroupService;

    @GetMapping
    public ResponseEntity<List<MachineGroupDto>> getAllMachineGroups() {
        List<MachineGroupDto> machineGroups = machineGroupService.getMachineGroups();
        return ResponseEntity.status(HttpStatus.OK).body(machineGroups);
    }

    @PostMapping
    public ResponseEntity<MachineGroupDto> addMachineGroup(@RequestBody MachineGroupDto machineGroupDto) {
        MachineGroupDto machineGroup = machineGroupService.addMachineGroup(machineGroupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(machineGroup);

    }

    @PutMapping("/{machineGroup_id}")
    public ResponseEntity<MachineGroupDto> updateMachineGroup(@PathVariable("machineGroup_id") String Id,
            @RequestBody MachineGroupDto machineGroupDto) {
        MachineGroupDto updatedmachineGroup = machineGroupService.updateMachineGroup(Id, machineGroupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedmachineGroup);
    }

    @DeleteMapping("/{machineGroup_id}")
    public ResponseEntity<Void> deleteMachineGroup(@PathVariable("machineGroup_id") String Id) {
        machineGroupService.deleteMachineGroup(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{machineGroup_id}")
    public ResponseEntity<MachineGroupDto> getMachineGroupById(@PathVariable("machineGroup_id") String Id) {
        MachineGroupDto machineGroup = machineGroupService.getMachineGroupById(Id);
        return ResponseEntity.ok(machineGroup);
    }
}
