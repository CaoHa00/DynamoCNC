package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.DrawingCode;

public interface DrawingCodeRepository extends JpaRepository<DrawingCode, String> {

}
