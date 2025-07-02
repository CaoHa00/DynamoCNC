package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Dynamo_Backend.entities.OrderDrawingCode;

@Repository
public interface OrderDrawingCodeRepository extends JpaRepository<OrderDrawingCode, String> {

}
