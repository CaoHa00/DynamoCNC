package com.example.Dynamo_Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.DrawingCode;

public interface DrawingCodeRepository extends JpaRepository<DrawingCode, String> {
    List<DrawingCode> findAllByStatus(Integer status);

    boolean existsByDrawingCodeName(String name);

    Optional<DrawingCode> findByDrawingCodeName(String name);
}
