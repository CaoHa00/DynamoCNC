package com.example.Dynamo_Backend.entities;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "machine_group", nullable = true)
    private String machineGroup;

    @Column(name = "machine_office", nullable = true)
    private String machineOffice;

    @Column(name = "status", nullable = true)
    private int status;
    @Column(name = "add_date", nullable = true)
    private String addDate;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "machine-group")
    private List<MachineGroup> machineGroups;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "machine-process")
    private List<DrawingCodeProcess> drawingCodeProcesses;
}
