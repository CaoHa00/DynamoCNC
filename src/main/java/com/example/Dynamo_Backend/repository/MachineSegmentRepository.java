package com.example.Dynamo_Backend.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.entities.MachineSegment;

public interface MachineSegmentRepository extends JpaRepository<MachineSegment, Long> {
  Optional<MachineSegment> findTopByMachineIdOrderByStartTimeDesc(Integer machineId);

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
}
