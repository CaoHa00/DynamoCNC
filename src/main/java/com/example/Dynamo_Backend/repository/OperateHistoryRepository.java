package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.OperateHistory;

public interface OperateHistoryRepository extends JpaRepository<OperateHistory, String> {
    List<OperateHistory> findByDrawingCodeProcess_processId(String processId);

    List<OperateHistory> findByStaff_Id(String staffId);
}
