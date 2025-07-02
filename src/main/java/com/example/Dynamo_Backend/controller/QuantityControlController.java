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
import com.example.Dynamo_Backend.dto.QuantityControlDto;

import com.example.Dynamo_Backend.service.QuantityControlService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/quantity-control")
public class QuantityControlController {
    public final QuantityControlService quantityControlService;

    @GetMapping
    public ResponseEntity<List<QuantityControlDto>> getAllQuantityControls() {
        List<QuantityControlDto> quantityControlDtos = quantityControlService.getAllQuantityControl();
        return ResponseEntity.status(HttpStatus.OK).body(quantityControlDtos);
    }

    @PostMapping
    public ResponseEntity<QuantityControlDto> addQuantityControl(@RequestBody QuantityControlDto quantityControlDto) {
        QuantityControlDto quantityControl = quantityControlService.addQuantityControl(quantityControlDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(quantityControl);
    }

    @PutMapping
    public ResponseEntity<QuantityControlDto> updateQuantityControl(@PathVariable("quantityControl_id") String Id,
            @RequestBody QuantityControlDto quantityControlDto) {
        QuantityControlDto quantityControl = quantityControlService.updateQuantityControl(Id, quantityControlDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(quantityControl);
    }

    @DeleteMapping("/{quantityControl_id}")
    public ResponseEntity<Void> deleteQuantityControl(@PathVariable("quantityControl_id") String Id) {
        quantityControlService.deleteQuantityControl(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{quantityControl_id}")
    public ResponseEntity<QuantityControlDto> getQuantityControlById(@PathVariable("quantityControl_id") String Id) {
        QuantityControlDto quantityControl = quantityControlService.getQuantityControlById(Id);
        return ResponseEntity.ok(quantityControl);
    }
}
