package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Dynamo_Backend.entities.Operator;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, String> {
    void deleteByIdIn(List<String> ids);

}
