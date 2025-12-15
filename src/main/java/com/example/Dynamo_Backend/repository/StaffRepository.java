package com.example.Dynamo_Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Dynamo_Backend.entities.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {
    void deleteByIdIn(List<String> ids);

    Optional<Staff> findByStaffId(Integer staffId);

    boolean existsByStaffId(Integer staffId);

    List<Staff> findAllByStatus(Integer status);

}
