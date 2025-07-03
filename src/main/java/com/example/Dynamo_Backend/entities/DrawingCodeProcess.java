package com.example.Dynamo_Backend.entities;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DrawingCodeProcess {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String processId;

    @Column(name = "part_number", nullable = false)
    private Integer partNumber;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(name = "manufacturing_point", nullable = false)
    private Integer manufacturingPoint;

    @Column(name = "pg_time", nullable = false)
    private Long pgTime;

    @Column(name = "start_time", nullable = true)
    private Long startTime;

    @Column(name = "end_time", nullable = true)
    private Long endTime;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "process_status", nullable = false)
    private Integer processStatus;
    // 1: to do
    // 2: in progress
    // 3: waiting for qc
    // 4:

    @Column(name = "qc_note", nullable = false)
    private String qcNote;

    @ManyToOne
    @JoinColumn(name = "drawing_code_id", nullable = false)
    @JsonBackReference(value = "drawing-code-process")
    private DrawingCode drawingCode;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = true)
    @JsonBackReference(value = "machine-process")
    private Machine machine;

    @ManyToOne
    @JoinColumn(name = "quality_control_id", nullable = true)
    @JsonBackReference(value = "quality-control-process")
    private QualityControl qualityControl;

    @OneToMany(mappedBy = "drawingCodeProcess", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "history-process")
    private List<OperateHistory> operateHistories;

    @OneToMany(mappedBy = "drawingCodeProcess", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "stats-process")
    private List<Log> logs;

}
