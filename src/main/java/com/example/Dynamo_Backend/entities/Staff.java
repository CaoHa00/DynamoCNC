package com.example.Dynamo_Backend.entities;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String Id;

    @Column(name = "staff_id", nullable = false)
    private Integer staffId;

    @Column(name = "staff_name", nullable = false)
    private String staffName;

    @Column(name = "staff_office", nullable = false)
    private String staffOffice;

    @Column(name = "staff_section", nullable = false)
    private String staffSection;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;
    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "staff-group")
    private List<StaffGroup> staffGroups;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "history-staff")
    private List<OperateHistory> operateHistories;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "stats-staff")
    private List<Log> statstistics;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<StaffKpi> staffKpis;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Plan> plans;

    // @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    // @JsonManagedReference
    // private List<OrderDetail> orderDetails;
}
