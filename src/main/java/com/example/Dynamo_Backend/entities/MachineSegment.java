package com.example.Dynamo_Backend.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Index;

@Entity
@Table(name = "machine_segment", indexes = {
        @Index(name = "idx_segment_machine_start", columnList = "machine_id, start_time"),
        @Index(name = "idx_segment_machine_date_shift", columnList = "machine_id, log_date, shift_code"),
        @Index(name = "idx_segment_log_date", columnList = "log_date")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer machineId;

    private String status; // R1, S1, E1
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationSeconds;
    private LocalDate logDate;
    private String processId;
    private String processType;
    private String shiftCode; // CA_NGAY, CA_DEM
    private int aggregated = 0;
}
