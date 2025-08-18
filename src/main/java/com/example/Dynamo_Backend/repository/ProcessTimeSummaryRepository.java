package com.example.Dynamo_Backend.repository;

import com.example.Dynamo_Backend.entities.ProcessTimeSummary;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessTimeSummaryRepository extends JpaRepository<ProcessTimeSummary, Integer> {
    Optional<ProcessTimeSummary> findByOrderDetail_OrderDetailId(String orderDetailId);
}