package com.example.Dynamo_Backend.entities;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "current_status")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrentStatus {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String Id;
    @Column(name = "machineId", nullable = false)
    private String machineId;
    @Column(name = "staffId", nullable = true)
    private String staffId;
    @Column(name = "processId", nullable = true)
    private String processId;
    @Column(name = "time", nullable = false)
    private String time;
    @Column(name = "status", nullable = false)
    private String status;
}
