package com.coding.vaulthometask.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customer_daily_weekly_stats")
@Data
public class CustomerDailyWeeklyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;

    private LocalDate currentDay;

    @Column(precision = 19, scale = 5)
    private BigDecimal dailyTotal;

    @Column(precision = 19, scale = 5)
    private BigDecimal weeklyTotal;

    private Integer dailyCount;

    private LocalDate currentWeekMonday;

    public CustomerDailyWeeklyStats() {}

    public CustomerDailyWeeklyStats(String customerId, LocalDate currentDay, LocalDate currentWeekMonday) {
        this.customerId = customerId;
        this.currentDay = currentDay;
        this.dailyCount = 0;
        this.currentWeekMonday = currentWeekMonday;
        this.dailyTotal = BigDecimal.ZERO;
        this.weeklyTotal = BigDecimal.ZERO;
    }
}
