package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.TempStartTime;

public interface TempStartTimeRepository extends JpaRepository<TempStartTime, String> {
    TempStartTime findByMachineId(Integer machineId);
}
