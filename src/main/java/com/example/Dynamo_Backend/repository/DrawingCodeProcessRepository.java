package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.entities.DrawingCodeProcess;

public interface DrawingCodeProcessRepository extends JpaRepository<DrawingCodeProcess, String> {
    List<DrawingCodeProcess> findByMachine_MachineId(Integer machineId);

    List<DrawingCodeProcess> findByIsPlan(Integer planned);

    List<DrawingCodeProcess> findByProcessStatus(Integer processStatus);

    @Query("SELECT p FROM DrawingCodeProcess p WHERE p.startTime <= :endTime AND p.endTime >= :startTime")
    List<DrawingCodeProcess> findProcessesInRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
