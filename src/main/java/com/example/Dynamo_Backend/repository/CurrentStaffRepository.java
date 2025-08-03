package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.CurrentStaff;

public interface CurrentStaffRepository extends JpaRepository<CurrentStaff, Integer> {
    CurrentStaff findByMachine_MachineId(Integer machineId);
}
