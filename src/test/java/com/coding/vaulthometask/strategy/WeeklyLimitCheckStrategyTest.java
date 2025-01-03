package com.coding.vaulthometask.strategy;

import com.coding.vaulthometask.exception.WeeklyLimitExceededException;
import com.coding.vaulthometask.model.CustomerDailyWeeklyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeeklyLimitCheckStrategyTest {

    private WeeklyLimitCheckStrategy weeklyStrategy;

    @BeforeEach
    void setUp() {
        weeklyStrategy = new WeeklyLimitCheckStrategy();
    }

    @Test
    void testCheckLimit_WeeklyNotExceeded() {
        CustomerDailyWeeklyStats stats = new CustomerDailyWeeklyStats();
        stats.setWeeklyTotal(new BigDecimal("19000.00")); // $19000
        LimitCheckContext context = new LimitCheckContext(
                "customer2",
                new BigDecimal("500.00"), // $500
                Instant.now(),
                stats
        );
        // 19000 + 500 = 19500 which is <= 20000
        assertDoesNotThrow(() -> weeklyStrategy.checkLimit(context),
                "No exception should be thrown if weekly total + amount <= $20000");
    }

    @Test
    void testCheckLimit_WeeklyExceeded() {
        // Given
        CustomerDailyWeeklyStats stats = new CustomerDailyWeeklyStats();
        stats.setWeeklyTotal(new BigDecimal("19900.00")); // $19900
        LimitCheckContext context = new LimitCheckContext(
                "customer2",
                new BigDecimal("200.00"), // $200
                Instant.now(),
                stats
        );
        // 19900 + 200 = 20100 > 20000
        assertThrows(WeeklyLimitExceededException.class,
                () -> weeklyStrategy.checkLimit(context),
                "Should throw WeeklyLimitExceededException if weekly total + amount > $20000");
    }
}
