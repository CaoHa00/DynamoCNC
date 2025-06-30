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

import com.example.Dynamo_Backend.dto.OperatorDto;
import com.example.Dynamo_Backend.service.OperatorService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/operator")
public class OperatorController {
    public final OperatorService operatorService;

    @GetMapping
    public ResponseEntity<List<OperatorDto>> getAllOperators() {
        List<OperatorDto> operators = operatorService.getAllOperators();
        return ResponseEntity.status(HttpStatus.OK).body(operators);
    }

    @PostMapping
    public ResponseEntity<OperatorDto> addOperator(@RequestBody OperatorDto operatorDto) {
        OperatorDto operator = operatorService.addOperator(operatorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(operator);

    }

    @PutMapping("/{operator_id}")
    public ResponseEntity<OperatorDto> updateOperator(@PathVariable("operator_id") String Id,
            @RequestBody OperatorDto operatorDto) {
        OperatorDto updatedOperator = operatorService.updateOperator(Id, operatorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedOperator);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteOperators(@RequestBody List<String> operatorIds) {
        operatorService.deleteOperator(operatorIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{operator_id}")
    public ResponseEntity<OperatorDto> getOperatorById(@PathVariable("operator_id") String Id) {
        OperatorDto operator = operatorService.getOperatorById(Id);
        return ResponseEntity.ok(operator);
    }

}
