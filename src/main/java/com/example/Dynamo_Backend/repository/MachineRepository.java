package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Machine;

public interface MachineRepository extends JpaRepository<Machine, Integer> {

}
