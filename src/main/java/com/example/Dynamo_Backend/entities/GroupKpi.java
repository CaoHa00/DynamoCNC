package com.example.Dynamo_Backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GroupKpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Column(name = "year", nullable = false)
    private Integer year;
    @Column(name = "month", nullable = false)
    private Integer month;
    @Column(name = "week", nullable = true)
    private Integer week;
    @Column(name = "isMonth", nullable = false)
    private Integer isMonth;
    @Column(name = "office", nullable = false)
    private String office;
    @Column(name = "working_hour_goal", nullable = false)
    private Integer workingHourGoal;
    @Column(name = "working_hour_difference", nullable = false)
    private Integer workingHourDifference;
    @Column(name = "working_hour", nullable = false)
    private Integer workingHour;
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference
    private Group group;
    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;
}
