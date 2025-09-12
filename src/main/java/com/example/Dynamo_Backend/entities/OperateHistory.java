package com.example.Dynamo_Backend.entities;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "operate_history")
public class OperateHistory {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String operateHistoryId;

    @Column(name = "manufacturing_point", nullable = false)
    private Integer manufacturingPoint;

    @Column(name = "pg_time", nullable = true)
    private Float pgTime;

    @Column(name = "start_time", nullable = false)
    private Long startTime;

    @Column(name = "stop_time", nullable = false)
    private Long stopTime;

    @Column(name = "in_progress", nullable = false)
    private Integer inProgress;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonBackReference(value = "history-staff")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "process_id", nullable = false)
    @JsonBackReference(value = "history-process")
    private DrawingCodeProcess drawingCodeProcess;

}
