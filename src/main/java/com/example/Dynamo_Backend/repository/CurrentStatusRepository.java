package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.CurrentStatus;

public interface CurrentStatusRepository extends JpaRepository<CurrentStatus, String> {
    CurrentStatus findByMachineId(String machineId);
}
