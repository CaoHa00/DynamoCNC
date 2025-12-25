package com.example.Dynamo_Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Dynamo_Backend.entities.GroupKpi;

public interface GroupKpiRepository extends JpaRepository<GroupKpi, Integer> {

        Optional<GroupKpi> findByGroup_GroupIdAndWeekAndYear(String groupId, Integer week,
                        Integer year);

        Optional<GroupKpi> findByGroup_GroupIdAndIsMonthAndMonthAndYear(String groupId, Integer isMonth, Integer month,
                        Integer year);

        Optional<GroupKpi> findByGroup_GroupIdAndMonthAndYearAndIsMonth(String groupId, Integer month, Integer year,
                        Integer isMonth);

        Optional<GroupKpi> findByGroup_GroupIdAndYearAndWeekAndIsMonth(String groupId, Integer year, Integer week,
                        Integer isMonth);

        List<GroupKpi> findByYearAndWeekAndIsMonth(Integer year, Integer week,
                        Integer isMonth);
}
