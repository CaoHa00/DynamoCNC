package com.example.Dynamo_Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.entities.Machine;

public interface MachineRepository extends JpaRepository<Machine, Integer> {
    // List<Machine> findByGroup_GroupId(String groupId);

    // List<Machine> findByGroup_GroupName(String groupName);

    List<Machine> findAllByStatus(Integer status);

    Optional<Machine> findByMachineName(String name);

    boolean existsByMachineName(String name);

    @Query(value = "SELECT DISTINCT m.* " +
            "FROM machine m " +
            "JOIN machine_kpi k ON m.machine_id = k.machine_id " +
            "WHERE k.group_id = :groupId " +
            "AND k.year = (SELECT TOP 1 k2.year FROM machine_kpi k2 WHERE k2.group_id = :groupId ORDER BY CASE WHEN k2.year = :year AND k2.month = :month THEN 0 ELSE 1 END, k2.year DESC, k2.month DESC) "
            +
            "AND k.month = (SELECT TOP 1 k2.month FROM machine_kpi k2 WHERE k2.group_id = :groupId ORDER BY CASE WHEN k2.year = :year AND k2.month = :month THEN 0 ELSE 1 END, k2.year DESC, k2.month DESC)", nativeQuery = true)
    List<Machine> findMachinesByGroupIdLatestOrCurrent(
            @Param("groupId") String groupId,
            @Param("year") int year,
            @Param("month") int month);

    @Query(value = """
            WITH ordered_log AS (
                SELECT
                    log_id,
                    machine_id,
                    status,
                    time_stamp,
                    LEAD(time_stamp) OVER (PARTITION BY machine_id ORDER BY time_stamp) AS next_time_stamp
                FROM log
                WHERE machine_id = :machineId
                  AND time_stamp BETWEEN :startTime AND :endTime
            ),
            durations AS (
                SELECT
                    status,
                    SUM(next_time_stamp - time_stamp) AS total_duration_ms
                FROM ordered_log
                WHERE next_time_stamp IS NOT NULL
                GROUP BY status
            ),
            all_statuses AS (
                SELECT '0' AS status
                UNION ALL SELECT 'R1'
                UNION ALL SELECT 'R2'
                UNION ALL SELECT 'S1'
                UNION ALL SELECT 'S2'
                UNION ALL SELECT 'E1'
                UNION ALL SELECT 'E2'
            )
            SELECT
                ROUND(COALESCE(d.total_duration_ms, 0) / 3600000.0, 2) AS totalDurationHr
            FROM all_statuses a
            LEFT JOIN durations d ON a.status = d.status
            ORDER BY a.status
            """, nativeQuery = true)
    List<Float> calculateDurationsByStatusAndRange(
            @Param("machineId") Integer machineId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime);

}
