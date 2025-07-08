package com.example.Dynamo_Backend.entities;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff_kpi")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StaffKpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "duration", nullable = false)
    private String duration;

    @Column(name = "year", nullable = false)
    private int yeat;

    @Column(name = "month", nullable = true)
    private int month;

    @Column(name = "week", nullable = true)
    private int week;

    @Column(name = "pg_time_goal", nullable = false)
    private float pgTimeGoal;

    @Column(name = "machine_time_goal", nullable = false)
    private float machineTimeGoal;
    @Column(name = "manufacturing_point", nullable = false)
    private float manufacturingPoint;
    @Column(name = "ole_goal", nullable = false)
    private float oleGoal;
    @Column(name = "kpi", nullable = false)
    private float kpi;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonBackReference
    private Staff staff;
}
