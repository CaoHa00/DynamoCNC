package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.entities.Machine;

public interface MachineRepository extends JpaRepository<Machine, Integer> {
  // List<Machine> findByGroup_GroupId(String groupId);

  // List<Machine> findByGroup_GroupName(String groupName);

  List<Machine> findAllByStatus(Integer status);

  @Query(value = "SELECT DISTINCT m.* " +
      "FROM machine m " +
      "JOIN machine_kpi k ON m.machine_id = k.id " +
      "WHERE k.group_id = :groupId " +
      "AND k.year = (SELECT TOP 1 k2.year FROM machine_kpi k2 WHERE k2.group_id = :groupId ORDER BY CASE WHEN k2.year = :year AND k2.month = :month THEN 0 ELSE 1 END, k2.year DESC, k2.month DESC) "
      +
      "AND k.month = (SELECT TOP 1 k2.month FROM machine_kpi k2 WHERE k2.group_id = :groupId ORDER BY CASE WHEN k2.year = :year AND k2.month = :month THEN 0 ELSE 1 END, k2.year DESC, k2.month DESC)", nativeQuery = true)
  List<Machine> findMachinesByGroupIdLatestOrCurrent(
      @Param("groupId") String groupId,
      @Param("year") int year,
      @Param("month") int month);
}
