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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order-drawing-code")
public class OrderDrawingCode {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String orderDrawingCodeId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference(value = "order-drawing-code")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "drawing_code_id", nullable = false)
    @JsonBackReference(value = "drawing-code-order")
    private DrawingCode drawingCode;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;
    @Column(name = "updatedDate", nullable = false)
    private long updatedDate;

    @Column(name = "status", nullable = false)
    private int status;

}
