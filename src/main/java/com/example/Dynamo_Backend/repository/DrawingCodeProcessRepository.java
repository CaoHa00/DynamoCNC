package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.dto.ResponseDto.PartProgressDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.OrderDetail;

public interface DrawingCodeProcessRepository extends JpaRepository<DrawingCodeProcess, String> {
  List<DrawingCodeProcess> findByMachine_MachineIdAndStatus(Integer machineId, Integer status);

  List<DrawingCodeProcess> findByOrderDetail_OrderDetailIdAndStatusAndProcessStatusNot(String orderDetailId,
      int status,
      int process);

  List<DrawingCodeProcess> findByStatus(Integer status);

  DrawingCodeProcess findByMachine_MachineIdAndProcessStatus(Integer machineId, Integer processStatus);

  Page<DrawingCodeProcess> findByIsPlanAndProcessStatusNotAndStatus(
      Integer isPlan,
      Integer processStatusNot,
      Integer status,
      Pageable pageable);

  List<DrawingCodeProcess> findByProcessStatus(Integer processStatus);

  @Query("SELECT p FROM DrawingCodeProcess p " +
      "LEFT JOIN p.plan pl " +
      "LEFT JOIN p.machine m " +
      "WHERE (m.machineId = :machineId OR pl.machine.machineId = :machineId) and p.status = 1")
  List<DrawingCodeProcess> findByMachineOrPlanMachine(@Param("machineId") Integer machineId);

  @Query("SELECT p FROM DrawingCodeProcess p WHERE p.startTime <= :endTime AND p.endTime >= :startTime")
  List<DrawingCodeProcess> findProcessesInRange(@Param("startTime") Long startTime,
      @Param("endTime") Long endTime);

  @Query("""
          SELECT oh.drawingCodeProcess
          FROM OperateHistory oh
          WHERE (:staffId IS NULL OR oh.staff.id = :staffId)
          AND oh.drawingCodeProcess.processStatus = 3
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
            AND (:start IS NULL OR :stop IS NULL OR (dcp.endTime BETWEEN :start AND :stop))
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

  // find DrawingCodeProcess by managerGroup of orderDetail and in range
  @Query("SELECT p FROM DrawingCodeProcess p WHERE p.orderDetail.managerGroup.groupId = :groupId AND p.startTime <= :endTime AND p.endTime >= :startTime")
  List<DrawingCodeProcess> findProcessesByManagerGroupInRange(@Param("groupId") String groupId,
      @Param("startTime") Long startTime, @Param("endTime") Long endTime);

  // @Query("""
  // SELECT new com.example.Dynamo_Backend.dto.ResponseDto.PartProgressDto(
  // od.orderDetailId,
  // od.orderCode,
  // dcp.partNumber,
  // COUNT(dcp.processId),
  // SUM(CASE WHEN dcp.processStatus = 3 THEN 1 ELSE 0 END),
  // SUM(CASE WHEN dcp.processStatus = 2 THEN 1 ELSE 0 END)
  // )
  // FROM DrawingCodeProcess dcp
  // JOIN dcp.orderDetail od
  // GROUP BY od.orderDetailId, od.orderCode, dcp.partNumber
  // ORDER BY od.orderDetailId, dcp.partNumber
  // """)
  // List<PartProgressDto> getPartProgress();

  @Query("""
          SELECT new com.example.Dynamo_Backend.dto.ResponseDto.PartProgressDto(
              od.orderDetailId,
              od.orderCode,
              dcp.partNumber,
              COUNT(dcp.processId),
              SUM(CASE WHEN dcp.processStatus = 3 THEN 1 ELSE 0 END),
              SUM(CASE WHEN dcp.processStatus = 2 THEN 1 ELSE 0 END)
          )
          FROM DrawingCodeProcess dcp
          JOIN dcp.orderDetail od
          WHERE od.orderDetailId IN :ids
          GROUP BY od.orderDetailId, od.orderCode, dcp.partNumber
          ORDER BY od.orderDetailId, dcp.partNumber
      """)
  List<PartProgressDto> getPartProgressByOrderDetailIds(
      @Param("ids") List<String> ids);
}
