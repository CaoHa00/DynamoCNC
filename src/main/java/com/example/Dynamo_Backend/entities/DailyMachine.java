package com.example.Dynamo_Backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class DailyMachine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "machine_id", nullable = false)
    private String machineName;

    @Column(name = "date", nullable = false)
    private String dateTime;

    @Column(name = "total_run", nullable = false)
    private String totalRunTime;
    @Column(name = "total_stop", nullable = false)
    private String totalStop;
    @Column(name = "total_error", nullable = false)
    private String totalError;
    @Column(name = "total_empty", nullable = false)
    private String totalEmpty;
    @Column(name = "total_pg_goal", nullable = false)
    private String TotalPgGoal;
    @Column(name = "total_count", nullable = false)
    private String TotalCount;

}
