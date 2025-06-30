package com.example.Dynamo_Backend.entities;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operator")
public class Operator {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String Id;

    @Column(name = "operator_id", nullable = false)
    private Integer operatorId;

    @Column(name = "operator_name", nullable = false)
    private String operatorName;

    @Column(name = "operator_office", nullable = false)
    private String operatorOffice;

    @Column(name = "operator_section", nullable = false)
    private String operatorSection;

    @Column(name = "operator_step", nullable = false)
    private String operatorStep;

    @Column(name = "kpi", nullable = false)
    private Double kpi;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "operator-group")
    private List<OperatorGroup> operatorGroups;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "history-operator")
    private List<OperateHistory> operateHistories;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "stats-operator")
    private List<Log> statstistics;
}
