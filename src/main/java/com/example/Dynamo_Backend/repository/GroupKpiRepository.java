package com.example.Dynamo_Backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Dynamo_Backend.entities.GroupKpi;

public interface GroupKpiRepository extends JpaRepository<GroupKpi, Integer> {

    Optional<GroupKpi> findByGroup_GroupIdAndWeekAndMonthAndYear(String groupId, Integer week, Integer month,
            Integer year);

    Optional<GroupKpi> findByGroup_GroupIdAndIsMonthAndMonthAndYear(String groupId, Integer isMonth, Integer month,
            Integer year);
}
