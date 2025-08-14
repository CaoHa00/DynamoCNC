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

    // @Query("""
    // SELECT DISTINCT m
    // FROM Machine m
    // JOIN MachineKpi k ON m.id = k.machineId
    // WHERE k.groupId = :groupId
    // """)
    // List<Machine> findMachinesByGroupId(@Param("groupId") String groupId);

    @Query(value = """
                SELECT DISTINCT m.*
                FROM machine m
                JOIN machine_kpi k ON m.id = k.machine_id
                WHERE k.group_id = :groupId
                  AND (k.year, k.month) = (
                      SELECT k2.year, k2.month
                      FROM machine_kpi k2
                      WHERE k2.group_id = :groupId
                      ORDER BY
                        CASE WHEN k2.year = :year AND k2.month = :month THEN 0 ELSE 1 END,
                        k2.year DESC,
                        k2.month DESC
                      LIMIT 1
                  )
            """, nativeQuery = true)
    List<Machine> findMachinesByGroupIdLatestOrCurrent(
            @Param("groupId") String groupId,
            @Param("year") int year,
            @Param("month") int month);
}
