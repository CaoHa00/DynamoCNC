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
import jakarta.persistence.Index;

@Entity
@Table(name = "log", indexes = {
        @Index(name = "idx_log_machine_time", columnList = "machine_id, time_stamp")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Log {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String logId;
    @Column(name = "time_stamp", nullable = false)
    private Long timeStamp;
    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = true)
    @JsonBackReference(value = "stats-staff")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = true)
    @JsonBackReference(value = "stats-machine")
    private Machine machine;

}
