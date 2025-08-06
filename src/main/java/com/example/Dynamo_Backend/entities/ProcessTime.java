package com.example.Dynamo_Backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "process_time")
public class ProcessTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "span_time", nullable = false)
    private Float spanTime;
    @Column(name = "run_time", nullable = false)
    private Float runTime;
    @Column(name = "pg_time", nullable = false)
    private Float pgTime;
    @Column(name = "stop_time", nullable = false)
    private Float stopTime;
    @Column(name = "offset_time", nullable = false)
    private Float offsetTime;

    @OneToOne
    @JoinColumn(name = "drawing_code_process_id", nullable = false)
    @JsonBackReference
    private DrawingCodeProcess drawingCodeProcess;

}
