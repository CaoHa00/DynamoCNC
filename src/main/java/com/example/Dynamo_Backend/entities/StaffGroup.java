package com.example.Dynamo_Backend.entities;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.cglib.core.Block;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "staff_group")
@AllArgsConstructor
@NoArgsConstructor
public class StaffGroup {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String staffGroupId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference(value = "group-staff")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonBackReference(value = "staff-group")
    private Staff staff;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;

}
