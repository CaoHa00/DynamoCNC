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
import com.example.Dynamo_Backend.dto.QualityControlDto;

import com.example.Dynamo_Backend.service.QualityControlService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/quality-control")
public class QualityControlController {
    public final QualityControlService qualityControlService;

    @GetMapping
    public ResponseEntity<List<QualityControlDto>> getAllQualityControls() {
        List<QualityControlDto> qualityControlDtos = qualityControlService.getAllQualityControl();
        return ResponseEntity.status(HttpStatus.OK).body(qualityControlDtos);
    }

    @PostMapping
    public ResponseEntity<QualityControlDto> addQualityControl(@RequestBody QualityControlDto qualityControlDto) {
        QualityControlDto qualityControl = qualityControlService.addQualityControl(qualityControlDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(qualityControl);
    }

    @PutMapping
    public ResponseEntity<QualityControlDto> updateQualityControl(@PathVariable("qualityControl_id") String Id,
            @RequestBody QualityControlDto qualityControlDto) {
        QualityControlDto qualityControl = qualityControlService.updateQualityControl(Id, qualityControlDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(qualityControl);
    }

    @DeleteMapping("/{qualityControl_id}")
    public ResponseEntity<Void> deleteQualityControl(@PathVariable("qualityControl_id") String Id) {
        qualityControlService.deleteQualityControl(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{qualityControl_id}")
    public ResponseEntity<QualityControlDto> getQualityControlById(@PathVariable("qualityControl_id") String Id) {
        QualityControlDto qualityControl = qualityControlService.getQualityControlById(Id);
        return ResponseEntity.ok(qualityControl);
    }
}
