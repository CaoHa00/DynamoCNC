package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.ProcessTime;

public interface ProcessTimeRepository extends JpaRepository<ProcessTime, Integer> {

}
