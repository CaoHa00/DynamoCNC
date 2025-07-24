package com.example.Dynamo_Backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "current_staff", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "staff_id" }),
        @UniqueConstraint(columnNames = { "machine_id" })
})
public class CurrentStaff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff staff;

    @OneToOne
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @Column(name = "assigned_at", nullable = false)
    private Long assignedAt;
}
