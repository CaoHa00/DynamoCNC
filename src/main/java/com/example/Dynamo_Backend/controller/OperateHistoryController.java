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

import com.example.Dynamo_Backend.dto.OperateHistoryDto;
import com.example.Dynamo_Backend.service.OperateHistoryService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/operate-history")
public class OperateHistoryController {
    public final OperateHistoryService operateHistoryService;

    @GetMapping
    public ResponseEntity<List<OperateHistoryDto>> getAllOperateHistorys() {
        List<OperateHistoryDto> OperateHistorys = operateHistoryService.getAllOperateHistory();
        return ResponseEntity.status(HttpStatus.OK).body(OperateHistorys);
    }

    // @PostMapping
    // public ResponseEntity<OperateHistoryDto> addOperateHistory(@RequestBody
    // OperateHistoryDto operateHistoryDto) {
    // OperateHistoryDto operateHistory =
    // operateHistoryService.addOperateHistory(operateHistoryDto);
    // return ResponseEntity.status(HttpStatus.CREATED).body(operateHistory);

    // }

    // @PutMapping("/{operateHistory_id}")
    // public ResponseEntity<OperateHistoryDto>
    // updateOperateHistory(@PathVariable("operateHistory_id") String Id,
    // @RequestBody OperateHistoryDto operateHistoryDto) {
    // OperateHistoryDto updateOperateHistorys =
    // operateHistoryService.updateOperateHistory(Id, operateHistoryDto);
    // return ResponseEntity.status(HttpStatus.CREATED).body(updateOperateHistorys);
    // }

    @DeleteMapping("/{operateHistory_id}")
    public ResponseEntity<Void> deleteOperateHistory(@PathVariable("operateHistory_id") String Id) {
        operateHistoryService.deleteOperateHistory(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{operateHistory_id}")
    public ResponseEntity<OperateHistoryDto> getOperateHistoryById(@PathVariable("operateHistory_id") String Id) {
        OperateHistoryDto operateHistories = operateHistoryService.getOperateHistoryById(Id);
        return ResponseEntity.ok(operateHistories);
    }
}
