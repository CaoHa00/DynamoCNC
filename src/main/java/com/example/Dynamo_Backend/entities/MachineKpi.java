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
@Table(name = "machine_kpi")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MachineKpi {
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

    @Column(name = "oee_goal", nullable = false)
    private float oeeGoal;

    @Column(name = "monthly_running_time", nullable = false)
    private int monthlyRunningTime;
    @Column(name = "weekly_running_time", nullable = false)
    private int weeklyRunningTime;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = false)
    @JsonBackReference
    private Machine machine;
}
