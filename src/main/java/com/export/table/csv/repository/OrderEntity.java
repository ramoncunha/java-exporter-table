package com.export.table.csv.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table
@Entity
public class OrderEntity {
    private Long id;
    private Long userId;
    private Long paymentMethodId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String shippingAddress;
    private String trackingNumber;
    private String orderNumber;
}
