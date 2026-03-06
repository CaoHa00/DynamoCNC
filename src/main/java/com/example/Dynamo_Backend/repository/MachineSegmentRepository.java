package com.example.Dynamo_Backend.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.entities.MachineSegment;

import jakarta.persistence.LockModeType;

public interface MachineSegmentRepository extends JpaRepository<MachineSegment, Long> {
    Optional<MachineSegment> findTopByMachineIdOrderByStartTimeDesc(Integer machineId);

    @Query("""
            SELECT COALESCE(SUM(m.durationSeconds), 0)
            FROM MachineSegment m
            WHERE m.logDate = :logDate
              AND m.shiftCode = :shiftCode
              AND m.status = :status
              AND m.machineId = :machineId
            """)
    Long sumDurationByCondition(LocalDate logDate,
            String shiftCode,
            String status,
            Integer machineId);

    Optional<MachineSegment> findTopByMachineIdAndLogDateOrderByEndTimeDesc(
            Integer machineId,
            LocalDate logDate);

    MachineSegment findTopByMachineIdAndStartTimeBeforeOrderByStartTimeDesc(
            Integer machineId,
            LocalDateTime startTime);

    void deleteByMachineIdAndStartTimeBetween(
            Integer machineId,
            LocalDateTime startTime,
            LocalDateTime endTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT s FROM MachineSegment s
                WHERE s.machineId = :machineId
                ORDER BY s.startTime DESC
            """)
    List<MachineSegment> findLastForUpdate(Integer machineId, Pageable pageable);

    List<MachineSegment> findByMachineIdAndLogDate(Integer machineId, LocalDate logDate);

    List<MachineSegment> findByEndTimeIsNotNull();

    @Query(value = """
            SELECT
                status,
                SUM(duration_seconds) AS total_duration_seconds
            FROM machine_segment
            WHERE machine_id = :machineId
              AND start_time >= :fromTime
              AND end_time   <= :toTime
              AND duration_seconds IS NOT NULL
            GROUP BY status
            """, nativeQuery = true)
    List<Object[]> getGroupedDuration(
            @Param("machineId") Integer machineId,
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime);

    List<MachineSegment> findByMachineIdAndLogDateOrderByStartTimeAsc(Integer machineId, LocalDate logDate);
}
