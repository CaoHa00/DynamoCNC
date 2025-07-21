package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

}
