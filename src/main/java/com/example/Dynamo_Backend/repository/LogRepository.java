package com.example.Dynamo_Backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.Dynamo_Backend.entities.Log;
import com.example.Dynamo_Backend.repository.dto.MachineRunTimeDto;

public interface LogRepository extends JpaRepository<Log, String> {
        @Query(value = "SELECT TOP 5 l.machine_id AS machineId, " +
                        "SUM(l_next.next_time_stamp - l.time_stamp) / 3600000.0 AS totalRunTime " +
                        "FROM log l " +
                        "INNER JOIN ( " +
                        "    SELECT machine_id, time_stamp, " +
                        "           LEAD(time_stamp) OVER (PARTITION BY machine_id ORDER BY time_stamp) AS next_time_stamp "
                        +
                        "    FROM log " +
                        "    WHERE time_stamp BETWEEN :startTime AND :endTime " +
                        ") l_next ON l.machine_id = l_next.machine_id AND l.time_stamp = l_next.time_stamp " +
                        "WHERE l.status IN ('R1', 'R2') " +
                        "  AND l_next.next_time_stamp IS NOT NULL " +
                        "  AND l.machine_id IN ( " +
                        "      SELECT DISTINCT m.machine_id FROM machine_kpi m " +
                        "      WHERE m.group_id = :groupId AND m.month = :month AND m.year = :year " +
                        "  ) " +
                        "GROUP BY l.machine_id " +
                        "ORDER BY totalRunTime DESC", nativeQuery = true)
        List<MachineRunTimeDto> findTop5MachineRunTimeByGroupAndTime(
                        @Param("groupId") String groupId,
                        @Param("month") int month,
                        @Param("year") int year,
                        @Param("startTime") long startTime,
                        @Param("endTime") long endTime);

        // List<Log> findByDrawingCodeProcess_processIdOrderByTimeStampAsc(String
        // processId);

        // get all logs in range time by machineId
        List<Log> findByMachine_machineIdAndTimeStampBetweenOrderByTimeStampAsc(Integer machineId, Long startTime,
                        Long endTime);

        List<Log> findByMachine_machineIdInAndTimeStampBetweenOrderByTimeStampAsc(
                        List<Integer> machineIds, Long startTime, Long endTime);
}
