package com.example.Dynamo_Backend.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.entities.Group;

public interface GroupRepository extends JpaRepository<Group, String> {
        // List<Group> findByGroupType(String groupType);

        Optional<Group> findByGroupName(String groupName);

        // @Query("SELECT g FROM Group g JOIN g.machines m WHERE m.machineId =
        // :machineId")
        // Optional<Group> findByMachineId(String machineId);

        // @Query("SELECT g FROM Group g LEFT JOIN FETCH g.staffGroups WHERE g IN
        // (SELECT g2 FROM Group g2 JOIN g2.machines m WHERE m.machineId = :machineId)")
        // Optional<Group> findByMachineIdWithStaffGroups(String machineId);

        // @Query("SELECT g FROM Group g JOIN g.machines m WHERE m.machineId =
        // :machineId")
        // Optional<Group> findByMachineId(String machineId);

        @Query("""
                            SELECT g
                            FROM Group g
                            JOIN MachineKpi k ON g = k.group
                            WHERE k.machine.machineId = :machineId
                            ORDER BY
                                CASE WHEN k.year = :year AND k.month = :month THEN 0 ELSE 1 END,
                                k.year DESC,
                                k.month DESC
                        """)
        Optional<Group> findLatestByMachineId(
                        @Param("machineId") int machineId,
                        @Param("month") int month,
                        @Param("year") int year);
}
