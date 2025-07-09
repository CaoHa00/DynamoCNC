package com.example.Dynamo_Backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "protocol")
public class Protocol {
    @Id
    private String Id;

    @ManyToOne
    @JoinColumn(name = "process_id", nullable = false)
    @JsonBackReference(value = "protocol-process")
    private DrawingCodeProcess process;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonBackReference(value = "protocol-staff")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = false)
    @JsonBackReference(value = "protocol-machine")
    private Machine machine;

}
