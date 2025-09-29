package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.CurrentStatus;

public interface CurrentStatusRepository extends JpaRepository<CurrentStatus, String> {
    CurrentStatus findByMachineId(Integer machineId);

    List<CurrentStatus> findByStaffId(String staffId);

}
