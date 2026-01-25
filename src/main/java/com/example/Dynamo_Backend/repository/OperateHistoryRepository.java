package com.example.Dynamo_Backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.dto.ResponseDto.StaffSummary;
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
                JOIN FETCH oh.drawingCodeProcess dcp
                WHERE (:staffId IS NULL OR oh.staff.id = :staffId)
                  AND (:start IS NULL OR :stop IS NULL OR (oh.logDate BETWEEN :start AND :stop))
                ORDER BY oh.startTime DESC
            """)
    List<OperateHistory> findHistoriesByStaffAndTimeRange1(
            @Param("staffId") String staffId,
            @Param("start") LocalDate start,
            @Param("stop") LocalDate stop);

    @Query("""
                SELECT oh
                FROM OperateHistory oh
                WHERE (:staffId IS NULL OR oh.staff.id = :staffId)
                  AND (:fromDate IS NULL OR oh.logDate >= :fromDate)
                  AND (:toDate IS NULL OR oh.logDate <= :toDate)
            """)
    List<OperateHistory> findByStaffIdAndLogDate(
            @Param("staffId") String staffId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
                SELECT oh
                FROM OperateHistory oh
                WHERE oh.inProgress = 1
                  AND oh.drawingCodeProcess.processStatus = 2
                  AND oh.drawingCodeProcess.machine.machineId = :machineId
            """)
    List<OperateHistory> findInProgressByMachineId(@Param("machineId") Integer machineId);

    @Query(value = """
            SELECT
                SUM(oh.manufacturing_point)   AS totalManufacturingPoint,
                SUM(oh.pg_time)               AS totalPgTime,
                COUNT(DISTINCT oh.process_id) AS totalProcess,
                SUM(
                    DATEDIFF(SECOND, t.start_vn, t.stop_vn)
                ) AS totalDurationSeconds
            FROM operate_history oh
            CROSS APPLY (
                SELECT
                    DATEADD(HOUR, 7,
                        DATEADD(MILLISECOND, oh.start_time % 1000,
                            DATEADD(SECOND, oh.start_time / 1000, '1970-01-01')
                        )
                    ) AS start_vn,
                    DATEADD(HOUR, 7,
                        DATEADD(MILLISECOND, oh.stop_time % 1000,
                            DATEADD(SECOND, oh.stop_time / 1000, '1970-01-01')
                        )
                    ) AS stop_vn
            ) t
            WHERE oh.log_date BETWEEN :fromDate AND :toDate
              AND oh.staff_id IN (:staffIds)
              AND oh.stop_time > 0
            """, nativeQuery = true)
    StaffSummary getStaffKpi(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("staffIds") List<String> staffIds);

}
