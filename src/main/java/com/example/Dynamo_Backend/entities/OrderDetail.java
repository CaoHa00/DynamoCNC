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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_detail")
public class OrderDetail {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String orderDetailId;
    @Column(name = "order_code", nullable = false)
    private String orderCode;
    @Column(name = "order_type", nullable = false)
    private String orderType;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;
    @Column(name = "pg_time_goal", nullable = true)
    private Float pgTimeGoal;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "drawing_code_id", nullable = false)
    @JsonBackReference
    private DrawingCode drawingCode;

    @ManyToOne
    @JoinColumn(name = "manager_group_id", nullable = true)
    @JsonBackReference
    private Group managerGroup;
    // @Column(name = "status", nullable = false)
    // private int status;
    // @ManyToOne
    // @JoinColumn(name = "staff_id", nullable = false)
    // @JsonBackReference
    // private Staff staff;

    @OneToMany(mappedBy = "orderDetail", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DrawingCodeProcess> DrawingCodeProcesses;

}
