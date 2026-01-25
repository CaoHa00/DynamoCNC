package com.example.Dynamo_Backend.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.ProcessTimeShift;

public interface ProcessTimeShiftRepository extends JpaRepository<ProcessTimeShift, Long> {
    ProcessTimeShift findByMachineIdAndLogDateAndShiftCode(Integer machineId, LocalDate logDate, String shift);

}
