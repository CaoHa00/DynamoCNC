package com.example.Dynamo_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Dynamo_Backend.entities.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query(value = """
                SELECT COALESCE(SUM(hour_diff), 0)
                FROM Report
                WHERE date_time BETWEEN :fromTime AND :toTime
            """, nativeQuery = true)
    Integer getTotalHourDiff(
            @Param("fromTime") Long fromTime,
            @Param("toTime") Long toTime);
}
