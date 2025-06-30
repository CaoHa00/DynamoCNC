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

import com.example.Dynamo_Backend.dto.LogDto;
import com.example.Dynamo_Backend.service.LogService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/stats")
public class LogController {
    public final LogService statsSevice;

    @GetMapping
    public ResponseEntity<List<LogDto>> getAllStatss() {
        List<LogDto> stats = statsSevice.getAllLog();
        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }

    // @PostMapping
    // public ResponseEntity<LogDto> addStats(@RequestBody LogDto statsDto) {
    // LogDto stats = statsSevice.addLog(statsDto);
    // return ResponseEntity.status(HttpStatus.CREATED).body(stats);

    // }

    // @PutMapping("/{stats_id}")
    // public ResponseEntity<LogDto> updateStats(@PathVariable("stats_id") String
    // Id,
    // @RequestBody LogDto statsDto) {
    // LogDto updateStatss = statsSevice.updateLog(Id, statsDto);
    // return ResponseEntity.status(HttpStatus.CREATED).body(updateStatss);
    // }

    @DeleteMapping("/{stats_id}")
    public ResponseEntity<Void> deleteStats(@PathVariable("stats_id") String Id) {
        statsSevice.deleteLog(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{stats_id}")
    public ResponseEntity<LogDto> getStatsById(@PathVariable("stats_id") String Id) {
        LogDto stats = statsSevice.getLogById(Id);
        return ResponseEntity.ok(stats);
    }
}
