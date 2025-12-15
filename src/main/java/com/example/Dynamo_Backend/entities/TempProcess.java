package com.example.Dynamo_Backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "temp_process")
public class TempProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "process_id", nullable = false)
    private String processId;
    @Column(name = "machine_id", nullable = false)
    private Integer machineId;
    @Column(name = "pg_time", nullable = false)
    private Integer pgTime;
    @Column(name = "manufacturing_point", nullable = false)
    private Integer point;
}
