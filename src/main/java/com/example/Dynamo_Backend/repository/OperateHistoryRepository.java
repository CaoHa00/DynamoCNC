package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.entities.OperateHistory;

public interface OperateHistoryRepository extends JpaRepository<OperateHistory, String> {
  List<OperateHistory> findByDrawingCodeProcess_processId(String processId);

  List<OperateHistory> findByDrawingCodeProcess_processIdAndStaff_Id(String processId, String staffId);

  List<OperateHistory> findByStaff_Id(String staffId);

  @Query("""
          SELECT oh
          FROM OperateHistory oh
          JOIN FETCH oh.drawingCodeProcess dcp
          WHERE (:staffId IS NULL OR oh.staff.id = :staffId)
            AND (:start IS NULL OR :stop IS NULL OR (oh.startTime BETWEEN :start AND :stop))
          ORDER BY oh.startTime DESC
      """)
  List<OperateHistory> findHistoriesByStaffAndTimeRange(
      @Param("staffId") String staffId,
      @Param("start") Long start,
      @Param("stop") Long stop);

  @Query("""
          SELECT oh
          FROM OperateHistory oh
          WHERE oh.inProgress = 1
            AND oh.drawingCodeProcess.processStatus = 2
            AND oh.drawingCodeProcess.machine.machineId = :machineId
      """)
  List<OperateHistory> findInProgressByMachineId(@Param("machineId") Integer machineId);
}
