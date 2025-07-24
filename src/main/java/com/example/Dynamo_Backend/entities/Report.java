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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Column(name = "date_time", nullable = false)
    private long dateTime;
    @Column(name = "office", nullable = false)
    private String office;
    @Column(name = "report_type", nullable = false)
    private String reportType;
    @Column(name = "hour_diff", nullable = false)
    private Integer hourDiff;
    @Column(name = "created_date", nullable = false)
    private long createdDate;
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference
    private Group group;
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    @JsonBackReference
    private Admin admin;
}
