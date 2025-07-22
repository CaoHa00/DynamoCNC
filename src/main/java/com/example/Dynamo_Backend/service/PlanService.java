package com.example.Dynamo_Backend.service;

import java.util.List;

import com.example.Dynamo_Backend.dto.PlanDto;

public interface PlanService {
    PlanDto addPlan(PlanDto planDto);

    PlanDto updatePlan(Integer planId, PlanDto planDto);

    PlanDto getPlanById(Integer planId);

    void deletePlan(Integer planId);

    List<PlanDto> getAllPlan();
}
