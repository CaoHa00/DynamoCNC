package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Machine;

public interface MachineRepository extends JpaRepository<Machine, Integer> {
    List<Machine> findByGroup_GroupId(String groupId);
}
