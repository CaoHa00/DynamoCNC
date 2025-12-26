package com.example.Dynamo_Backend.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "machine_segment")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MachineSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "machine_id", nullable = false)
    private Integer machineId;

    @Column(name = "work_date")
    private LocalDate workDate;
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_time", nullable = false)
    private Long startTime;
    @Column(name = "end_time", nullable = false)
    private Long endTime;
    @Column(name = "duration", nullable = false)
    private Long duration;
    @Column(name = "shift", nullable = false)
    private String shift;

}
