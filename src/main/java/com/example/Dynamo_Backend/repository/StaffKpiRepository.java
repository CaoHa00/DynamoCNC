package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.StaffKpi;

public interface StaffKpiRepository extends JpaRepository<StaffKpi, Integer> {
    List<StaffKpi> findByStaff_staffId(Integer id);

    List<StaffKpi> findByStaff_Id(String id);

    StaffKpi findByStaff_IdAndMonthAndYear(String Id, Integer month, Integer year);
}
