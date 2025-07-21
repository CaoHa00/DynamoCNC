package com.example.Dynamo_Backend.entities;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.cglib.core.Block;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "group_")
public class Group {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_type")
    private String groupType;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Staff> staffs;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Machine> machines;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "group-staff")
    private List<StaffGroup> staffGroups;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "group-machine")
    private List<MachineGroup> machineGroups;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<GroupKpi> groupKpis;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Report> reports;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;

}
