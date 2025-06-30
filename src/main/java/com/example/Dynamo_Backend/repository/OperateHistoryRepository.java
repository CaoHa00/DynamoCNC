package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.OperateHistory;

public interface OperateHistoryRepository extends JpaRepository<OperateHistory, String> {

}
