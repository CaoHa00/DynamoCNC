package com.example.Dynamo_Backend.entities;

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
    @Column(name = "machine_name", nullable = false)
    private String status;

    @Column(name = "machine_name", nullable = false)
    private Long startTime;
    @Column(name = "machine_name", nullable = false)
    private Long endTime;
    @Column(name = "machine_name", nullable = false)
    private Long duration;
    @Column(name = "machine_name", nullable = false)
    private String shift;

}
