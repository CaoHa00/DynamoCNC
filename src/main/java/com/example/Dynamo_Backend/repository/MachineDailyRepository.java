package com.example.Dynamo_Backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.dto.MachineDailySummaryByMachine;
import com.example.Dynamo_Backend.dto.MachineRunTimeDto;
import com.example.Dynamo_Backend.entities.MachineDaily;

public interface MachineDailyRepository extends JpaRepository<MachineDaily, Long> {
    Optional<MachineDaily> findByMachineIdAndLogDateAndShiftCode(
            Integer machineId,
            LocalDate logDate,
            String shiftCode);

    @Query("""
                SELECT new com.example.Dynamo_Backend.dto.MachineDailySummaryByMachine(
                    SUM(md.runPgSeconds),
                    SUM(md.runOffsetSeconds),
                    SUM(md.emptySeconds),
                    SUM(md.stopSeconds),
                    SUM(md.errorSeconds),
                    SUM(md.mainProductSeconds),
                    SUM(md.reRunSeconds),
                    SUM(md.lkSeconds),
                    SUM(md.electricSeconds),
                    SUM(md.preparationSeconds),
                    SUM(md.mainProductPgSeconds),
                    SUM(md.electricPgSeconds),
                    SUM(md.otherSeconds),
                    SUM(md.spanSeconds),
                    SUM(md.expectedSeconds),
                    SUM(md.quantity)
                )
                FROM MachineDaily md
                WHERE md.machineId IN :machineIds
                    AND md.logDate BETWEEN :startDate AND :endDate
                    AND (
                            :shiftType = 'FULL'
                            OR md.shiftCode = :shiftType
                        )
            """)
    MachineDailySummaryByMachine sumByMachines(
            List<Integer> machineIds,
            LocalDate startDate,
            LocalDate endDate,
            String shiftType);

    @Query(value = """
                SELECT TOP 5
                    md.machine_id AS machineId,
                    m.machine_name AS machineName,
                    SUM(md.run_pg_seconds + md.run_offset_seconds) / 3600.0 AS totalRunTime
                FROM machine_daily md
                INNER JOIN machine_kpi mk
                    ON md.machine_id = mk.machine_id
                   AND mk.group_id = :groupId
                   AND mk.month = :month
                   AND mk.year = :year
                INNER JOIN machine m
                    ON md.machine_id = m.machine_id
                WHERE md.log_date BETWEEN :startDate AND :endDate
                  AND (
                        :shiftCode = 'FULL'
                        OR md.shift_code = :shiftCode
                      )
                GROUP BY md.machine_id, m.machine_name
                ORDER BY totalRunTime DESC
            """, nativeQuery = true)
    List<MachineRunTimeDto> findTop5MachineRunTimeFromDaily(
            @Param("groupId") String groupId,
            @Param("month") int month,
            @Param("year") int year,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("shiftCode") String shiftCode);

    @Query(value = """
                SELECT TOP 5
                    md.machine_id AS machineId,
                    m.machine_name AS machineName,
                    SUM(md.run_pg_seconds + md.run_offset_seconds) / 3600.0 AS totalRunTime
                FROM machine_daily md
                INNER JOIN machine_kpi mk
                    ON md.machine_id = mk.machine_id
                   AND mk.group_id = :groupId
                   AND mk.month = :month
                   AND mk.year = :year
                INNER JOIN machine m
                    ON md.machine_id = m.machine_id
                WHERE md.log_date BETWEEN :startDate AND :endDate
                  AND (
                        :shiftCode = 'FULL'
                        OR md.shift_code = :shiftCode
                      )
                GROUP BY md.machine_id, m.machine_name
                ORDER BY totalRunTime ASC
            """, nativeQuery = true)
    List<MachineRunTimeDto> findTop5MachineRunTimeFromDailyLow(
            @Param("groupId") String groupId,
            @Param("month") int month,
            @Param("year") int year,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("shiftCode") String shiftCode);

}
