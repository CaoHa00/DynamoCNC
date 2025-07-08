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
@Table(name = "tbl-order")
public class Order {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "CHAR(36)")
    private String orderId;
    @Column(name = "PO_number", nullable = false)
    private String poNumber;
    // @Column(name = "customer_name", nullable = false)
    // private String customerName;
    // @Column(name = "phone_number", nullable = false)
    // private String phoneNumber;
    // @Column(name = "address", nullable = false)
    // private String address;

    // @Column(name = "order_date", nullable = false)
    // private Long orderDate;
    // @Column(name = "delivery_date", nullable = false)
    // private Long deliveryDate;
    // @Column(name = "completion_date", nullable = false)
    // private Long completionDate;
    // @Column(name = "shippingMethod", nullable = false)
    // private String shippingMethod;
    @Column(name = "remark", nullable = false)
    private String remark;
    @Column(name = "status", nullable = false)
    private int status;
    @Column(name = "order_status", nullable = false)
    private int orderstatus;

    @Column(name = "created_date", nullable = false)
    private Long createdDate;
    @Column(name = "updated_date", nullable = false)
    private Long updatedDate;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderDetail> orderDetails;
}
