package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.Order;

public interface OrderRepository extends JpaRepository<Order, String> {

}
