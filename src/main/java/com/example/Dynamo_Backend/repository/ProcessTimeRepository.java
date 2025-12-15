package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.ProcessTime;

public interface ProcessTimeRepository extends JpaRepository<ProcessTime, Integer> {
    List<ProcessTime> findAllByDrawingCodeProcess_OrderDetail_OrderDetailId(String orderDetailId);

    ProcessTime findByDrawingCodeProcess_ProcessId(String processId);
}
