package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.TempProcess;

public interface TempProcessRepository extends JpaRepository<TempProcess, Integer> {
    TempProcess findByProcessId(String processId);

    TempProcess findByMachineId(Integer machineId);
}
