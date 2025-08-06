package com.example.Dynamo_Backend.entities;

import java.util.List;

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
@Table(name = "machine")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer machineId;

    @Column(name = "machine_name", nullable = false)
    private String machineName;

    @Column(name = "machine_type", nullable = false)
    private String machineType;

    // Consider as machineId, change name later
    @Column(name = "machine_group", nullable = true)
    private String machineGroup;

    @Column(name = "machine_office", nullable = true)
    private String machineOffice;

    @Column(name = "status", nullable = true)
    private int status;
    // 0:empty
    // 1:running
    // 2:stop
    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "machine-group")
    private List<MachineGroup> machineGroups;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference
    private Group group;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "machine-process")
    private List<DrawingCodeProcess> drawingCodeProcesses;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<MachineKpi> machineKpis;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Plan> plans;
}
