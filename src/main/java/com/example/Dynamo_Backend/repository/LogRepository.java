package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Log;

public interface LogRepository extends JpaRepository<Log, String> {
    List<Log> findByDrawingCodeProcess_processIdOrderByTimeStampAsc(String processId);
}
