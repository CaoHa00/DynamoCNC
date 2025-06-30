package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.MachineGroup;

public interface MachineGroupRepository extends JpaRepository<MachineGroup, String> {

}
