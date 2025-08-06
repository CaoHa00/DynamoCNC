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

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "oee_goal", nullable = false)
    private Float oee;

    @Column(name = "machine_mining_target", nullable = false)
    private Float machineMiningTarget;

    @Column(name = "createdDate", nullable = false)
    private Long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private Long updatedDate;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = false)
    @JsonBackReference
    private Machine machine;
}
