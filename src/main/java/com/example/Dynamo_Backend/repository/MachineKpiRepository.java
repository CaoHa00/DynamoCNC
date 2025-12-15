package com.example.Dynamo_Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Dynamo_Backend.entities.MachineKpi;

public interface MachineKpiRepository extends JpaRepository<MachineKpi, Integer> {
    List<MachineKpi> findByMachine_machineId(Integer id);

    List<MachineKpi> findByGroup_groupId(String group);

    List<MachineKpi> findByGroup_groupIdAndMonthAndYear(String group, int month, int year);

    MachineKpi findByMachine_machineIdAndMonthAndYear(Integer id, int month, int year);

}
