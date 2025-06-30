package com.example.Dynamo_Backend.entities;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "machine_group")
@AllArgsConstructor
@NoArgsConstructor
public class MachineGroup {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String machineGroupId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference(value = "group-machine")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = false)
    @JsonBackReference(value = "machine-group")
    private Machine machine;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;

}
