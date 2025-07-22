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

import com.example.Dynamo_Backend.dto.PlanDto;
import com.example.Dynamo_Backend.service.PlanService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/plan")
public class PlanController {
    @Autowired
    PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        List<PlanDto> planDtos = planService.getAllPlan();
        return ResponseEntity.status(HttpStatus.OK).body(planDtos);
    }

    @GetMapping("/{plan_id}")
    public ResponseEntity<PlanDto> getPlanById(@PathVariable("plan_id") Integer Id) {
        PlanDto planDto = planService.getPlanById(Id);
        return ResponseEntity.status(HttpStatus.OK).body(planDto);
    }

    @PostMapping()
    public ResponseEntity<PlanDto> addPlan(@RequestBody PlanDto planDto) {
        PlanDto newPlan = planService.addPlan(planDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPlan);
    }

    @PutMapping("/{plan_id}")
    public ResponseEntity<PlanDto> updatePlan(@PathVariable("plan_id") Integer Id, @RequestBody PlanDto planDto) {
        PlanDto updatedPlanDto = planService.updatePlan(Id, planDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedPlanDto);
    }

    @DeleteMapping("/{plan_id}")
    public ResponseEntity<PlanDto> deletePlan(@PathVariable("plan_id") Integer Id) {
        planService.deletePlan(Id);
        return ResponseEntity.ok().build();
    }

}
