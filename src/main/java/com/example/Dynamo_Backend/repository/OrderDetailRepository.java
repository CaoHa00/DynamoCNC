package com.example.Dynamo_Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Dynamo_Backend.entities.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    Optional<OrderDetail> findByOrderCode(String orderCode);

    List<OrderDetail> findByCreatedDateBetween(Long startDate, Long endDate);

}
