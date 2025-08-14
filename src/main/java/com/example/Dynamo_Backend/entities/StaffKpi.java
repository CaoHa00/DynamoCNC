package com.example.Dynamo_Backend.entities;

import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.GenericGenerator;

import com.example.Dynamo_Backend.dto.StaffKpiDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff_kpi")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StaffKpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "pg_time_goal", nullable = false)
    private Float pgTimeGoal;

    @Column(name = "machine_time_goal", nullable = false)
    private Float machineTimeGoal;
    @Column(name = "manufacturing_point", nullable = false)
    private Float manufacturingPoint;
    @Column(name = "ole_goal", nullable = false)
    private Float oleGoal;
    @Column(name = "work_goal", nullable = false)
    private Float workGoal;
    @Column(name = "kpi", nullable = false)
    private Float kpi;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference(value = "group-staff-kpi")
    private Group group;

    @Column(name = "createdDate", nullable = false)
    private Long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private Long updatedDate;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonBackReference
    private Staff staff;

    public boolean isSameAs(StaffKpiDto dto) {
        return Objects.equals(this.getPgTimeGoal(), dto.getPgTimeGoal()) &&
                Objects.equals(this.getKpi(), dto.getKpi()) &&
                Objects.equals(this.getOleGoal(), dto.getOleGoal()) &&
                Objects.equals(this.getWorkGoal(), dto.getWorkGoal()) &&
                Objects.equals(this.getMachineTimeGoal(), dto.getMachineTimeGoal()) &&
                Objects.equals(this.getManufacturingPoint(), dto.getManufacturingPoint()) &&
                Objects.equals(this.getMonth(), dto.getMonth()) &&
                Objects.equals(this.getYear(), dto.getYear()) &&
                Objects.equals(this.getStaff().getId(), dto.getStaffId());

    }

}
