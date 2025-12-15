package com.example.Dynamo_Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByStatus(Integer status);

    boolean existsByPoNumber(String poNumber);

    Optional<Order> findByPoNumber(String poNumber);
}
