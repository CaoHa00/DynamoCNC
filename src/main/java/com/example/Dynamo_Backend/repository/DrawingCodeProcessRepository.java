package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.entities.DrawingCodeProcess;

public interface DrawingCodeProcessRepository extends JpaRepository<DrawingCodeProcess, String> {
        List<DrawingCodeProcess> findByMachine_MachineId(Integer machineId);

        List<DrawingCodeProcess> findByIsPlanAndProcessStatusNot(Integer planned, int status);

        List<DrawingCodeProcess> findByProcessStatus(Integer processStatus);

        @Query("SELECT p FROM DrawingCodeProcess p " +
                        "LEFT JOIN p.plan pl " +
                        "LEFT JOIN p.machine m " +
                        "WHERE (m.machineId = :machineId OR pl.machine.machineId = :machineId)")
        List<DrawingCodeProcess> findByMachineOrPlanMachine(@Param("machineId") Integer machineId);

        @Query("SELECT p FROM DrawingCodeProcess p WHERE p.startTime <= :endTime AND p.endTime >= :startTime")
        List<DrawingCodeProcess> findProcessesInRange(@Param("startTime") Long startTime,
                        @Param("endTime") Long endTime);

        @Query("""
                            SELECT oh.drawingCodeProcess
                            FROM OperateHistory oh
                            WHERE (:staffId IS NULL OR oh.staff.id = :staffId)
                              AND (:start IS NULL OR :stop IS NULL OR (oh.startTime BETWEEN :start AND :stop))
                        """)
        List<DrawingCodeProcess> findProcessesByStaffAndTimeRange(
                        @Param("staffId") String staffId,
                        @Param("start") Long start,
                        @Param("stop") Long stop);

        @Query("""
                            SELECT dcp
                            FROM DrawingCodeProcess dcp
                            WHERE (:machineId IS NULL OR dcp.machine.machineId = :machineId)
                              AND (:start IS NULL OR :stop IS NULL OR (dcp.startTime BETWEEN :start AND :stop))
                              AND dcp.processStatus = 3
                        """)
        List<DrawingCodeProcess> findCompletedProcessesByMachineAndTime(
                        @Param("machineId") Integer machineId,
                        @Param("start") Long start,
                        @Param("stop") Long stop);

        @Query("""
                        SELECT dcp
                        FROM DrawingCodeProcess dcp
                        WHERE dcp.processStatus = :status
                          AND (:start IS NULL OR :stop IS NULL OR (dcp.startTime BETWEEN :start AND :stop))
                        """)
        List<DrawingCodeProcess> findByStatusAndTimeRange(
                        @Param("status") Integer status,
                        @Param("start") Long start,
                        @Param("stop") Long stop);

        // find DrawingCodeProcess by machineId and in range
        @Query("SELECT p FROM DrawingCodeProcess p WHERE p.machine.machineId = :machineId AND p.startTime <= :endTime AND p.endTime >= :startTime")
        List<DrawingCodeProcess> findProcessesByMachineInRange(@Param("machineId") Integer machineId,
                        @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // find DrawingCodeProcess by machineId and in range
    @Query("SELECT p FROM DrawingCodeProcess p WHERE p.machine.machineId = :machineId AND p.startTime <= :endTime AND p.endTime >= :startTime")
    List<DrawingCodeProcess> findProcessesByMachineInRange(@Param("machineId") Integer machineId,
            @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // find DrawingCodeProcess by managerGroup of orderDetail and in range
    @Query("SELECT p FROM DrawingCodeProcess p WHERE p.orderDetail.managerGroup.groupId = :groupId AND p.startTime <= :endTime AND p.endTime >= :startTime")
    List<DrawingCodeProcess> findProcessesByManagerGroupInRange(@Param("groupId") String groupId,
            @Param("startTime") Long startTime, @Param("endTime") Long endTime);

}
