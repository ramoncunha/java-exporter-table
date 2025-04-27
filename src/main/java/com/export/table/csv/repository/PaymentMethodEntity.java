package com.export.table.csv.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Table
@Entity
public class PaymentMethodEntity {

    private Long id;
    private String name;
    private String type;
    private boolean active;
    private BigDecimal fee;
    private Integer processingDays;
}
