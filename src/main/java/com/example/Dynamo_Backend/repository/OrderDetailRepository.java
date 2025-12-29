package com.example.Dynamo_Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    Optional<OrderDetail> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String code);

    List<OrderDetail> findByCreatedDateBetween(Long startDate, Long endDate);

    Page<OrderDetail> findByStatusAndProgressNot(Integer status, Integer progress, Pageable pageable);

    List<OrderDetail> findByStatusAndProgressNot(Integer status, Integer progress);

    @Query("""
                SELECT od FROM OrderDetail od
                WHERE (:keyword IS NULL
                    OR LOWER(od.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(od.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
                ) AND od.status = 1 and od.progress <> 3
            """)
    Page<OrderDetail> search(
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
                SELECT od.orderDetailId
                FROM OrderDetail od
                WHERE od.status = 1
                  AND od.progress <> 3
                  AND (:keyword IS NULL
                       OR LOWER(od.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
                GROUP BY od.orderDetailId, od.updatedDate
                ORDER BY od.updatedDate DESC
            """)
    Page<String> findOrderDetailIds(
            @Param("keyword") String keyword,
            Pageable pageable);
}
