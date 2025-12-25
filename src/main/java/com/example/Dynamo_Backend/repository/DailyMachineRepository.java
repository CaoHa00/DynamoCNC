package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.DailyMachine;

public interface DailyMachineRepository extends JpaRepository<Long, DailyMachine> {

}
