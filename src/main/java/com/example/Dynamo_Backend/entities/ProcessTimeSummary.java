package com.example.Dynamo_Backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "process_time_summary")
public class ProcessTimeSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "production_step")
    private Integer productionStep;

    @Column(name = "manufacturing_point")
    private Float manufacturingPoint;

    @Column(name = "pg_time")
    private Float pgTime;

    @Column(name = "span_time")
    private Float spanTime;

    @Column(name = "run_time")
    private Float runTime;

    @Column(name = "stop_time")
    private Float stopTime;

    @Column(name = "offset_time")
    private Float offsetTime;

    @OneToOne
    @JoinColumn(name = "order_detail_id", nullable = false)
    private OrderDetail orderDetail;
}