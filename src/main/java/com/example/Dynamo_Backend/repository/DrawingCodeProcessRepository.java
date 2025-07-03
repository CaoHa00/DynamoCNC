package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.DrawingCodeProcess;

public interface DrawingCodeProcessRepository extends JpaRepository<DrawingCodeProcess, String> {
    List<DrawingCodeProcess> findByMachine_MachineId(Integer machineId);
}
