package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Plan;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
    Plan findByDrawingCodeProcess_ProcessId(String processId);
}
