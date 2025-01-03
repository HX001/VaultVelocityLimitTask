package com.coding.vaulthometask.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "load_attempt",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_load_per_customer", columnNames = {"loadId", "customerId"})
        })
@Data
@NoArgsConstructor
public class LoadAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;  // PK

    private String loadId;
    private String customerId;

    @Column(precision = 19, scale = 5)
    private BigDecimal amount;

    private Instant loadTime;

    public LoadAttempt(String loadId, String customerId, BigDecimal amount, Instant loadTime) {
        this.loadId = loadId;
        this.customerId = customerId;
        this.amount = amount;
        this.loadTime = loadTime;
    }
}
