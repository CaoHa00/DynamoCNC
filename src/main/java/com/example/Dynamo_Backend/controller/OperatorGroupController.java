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

import com.example.Dynamo_Backend.dto.OperatorGroupDto;
import com.example.Dynamo_Backend.service.OperatorGroupService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/operator-group")
public class OperatorGroupController {
    public final OperatorGroupService operatorGroupService;

    @GetMapping
    public ResponseEntity<List<OperatorGroupDto>> getAllOperatorGroups() {
        List<OperatorGroupDto> operatorGroups = operatorGroupService.getOperatorGroups();
        return ResponseEntity.status(HttpStatus.OK).body(operatorGroups);
    }

    @PostMapping
    public ResponseEntity<OperatorGroupDto> addOperatorGroup(@RequestBody OperatorGroupDto operatorGroupDto) {
        OperatorGroupDto operatorGroup = operatorGroupService.addOperatorGroup(operatorGroupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(operatorGroup);

    }

    @PutMapping("/{operatorGroup_id}")
    public ResponseEntity<OperatorGroupDto> updateOperatorGroup(@PathVariable("operatorGroup_id") String Id,
            @RequestBody OperatorGroupDto operatorGroupDto) {
        OperatorGroupDto updatedOperatorGroup = operatorGroupService.updateOperatorGroup(Id, operatorGroupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedOperatorGroup);
    }

    @DeleteMapping("/{operatorGroup_id}")
    public ResponseEntity<Void> deleteOperatorGroup(@PathVariable("operatorGroup_id") String Id) {
        operatorGroupService.deleteOperatorGroup(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{operatorGroup_id}")
    public ResponseEntity<OperatorGroupDto> getOperatorGroupById(@PathVariable("operatorGroup_id") String Id) {
        OperatorGroupDto operatorGroup = operatorGroupService.getOperatorGroupById(Id);
        return ResponseEntity.ok(operatorGroup);
    }
}
