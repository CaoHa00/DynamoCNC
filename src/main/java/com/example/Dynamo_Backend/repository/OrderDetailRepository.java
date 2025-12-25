package com.example.Dynamo_Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Dynamo_Backend.entities.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    Optional<OrderDetail> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String code);

    List<OrderDetail> findByCreatedDateBetween(Long startDate, Long endDate);

    Page<OrderDetail> findByStatusAndProgressNot(Integer status, Integer progress, Pageable pageable);

    List<OrderDetail> findByStatusAndProgressNot(Integer status, Integer progress);

}
