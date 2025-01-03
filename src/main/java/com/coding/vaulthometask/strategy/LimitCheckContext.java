package com.coding.vaulthometask.strategy;

import com.coding.vaulthometask.model.CustomerDailyWeeklyStats;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class LimitCheckContext {
    private final String customerId;
    private final BigDecimal loadAmount;
    private final Instant loadTime;
    private final CustomerDailyWeeklyStats stats;
}
