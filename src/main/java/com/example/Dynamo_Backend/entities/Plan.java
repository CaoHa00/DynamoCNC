package com.example.Dynamo_Backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "process_time")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Column(name = "start_time", nullable = false)
    private Long startTime;
    @Column(name = "end_time", nullable = false)
    private Long endTime;
    @Column(name = "status", nullable = false)
    private Integer status;
    @Column(name = "in_progress", nullable = false)
    private Integer inProgress;
    @Column(name = "remark", nullable = false)
    private Float remark;
    @Column(name = "remark_time", nullable = false)
    private Long remarkTime;
    @Column(name = "created_date", nullable = false)
    private Long createdDate;
    @Column(name = "updated_date", nullable = false)
    private Long updatedDate;

    @ManyToOne
    @JoinColumn(name = "drawing_code_process_id", nullable = false)
    @JsonBackReference
    private DrawingCodeProcess drawingCodeProcess;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonBackReference
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = false)
    @JsonBackReference
    private Machine machine;

    @ManyToOne
    @JoinColumn(name = "planner_id", nullable = false)
    @JsonBackReference
    private Admin planner;
}
