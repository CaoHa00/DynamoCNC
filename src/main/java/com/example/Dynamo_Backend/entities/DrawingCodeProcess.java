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
    private Integer pgTime;

    @Column(name = "start_time", nullable = true)
    private String startTime;

    @Column(name = "end_time", nullable = true)
    private String endTime;

    @Column(name = "addDate", nullable = false)
    private String addDate;

    @Column(name = "status", nullable = false)
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "drawing_code_id", nullable = false)
    @JsonBackReference(value = "drawing-code-process")
    private DrawingCode drawingCode;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = true)
    @JsonBackReference(value = "machine-process")
    private Machine machine;

    @OneToMany(mappedBy = "drawingCodeProcess", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "history-process")
    private List<OperateHistory> operateHistories;

    @OneToMany(mappedBy = "drawingCodeProcess", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "stats-process")
    private List<Log> logs;

}
