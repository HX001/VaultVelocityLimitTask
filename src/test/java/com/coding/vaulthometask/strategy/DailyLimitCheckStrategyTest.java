package com.coding.vaulthometask.strategy;

import com.coding.vaulthometask.exception.DailyLimitExceededException;
import com.coding.vaulthometask.model.CustomerDailyWeeklyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DailyLimitCheckStrategyTest {

    private DailyLimitCheckStrategy dailyStrategy;

    @BeforeEach
    void setUp() {
        dailyStrategy = new DailyLimitCheckStrategy();
    }

    @Test
    void testCheckLimit_DailyNotExceeded() {
        CustomerDailyWeeklyStats stats = new CustomerDailyWeeklyStats();
        stats.setDailyTotal(new BigDecimal("4900.00")); // $4900
        LimitCheckContext context = new LimitCheckContext(
                "customer1",
                new BigDecimal("50.00"),  // $50
                Instant.now(),
                stats
        );
        // 4900 + 50 = 4950 which is <= 5000
        assertDoesNotThrow(() -> dailyStrategy.checkLimit(context),
                "Should not throw exception if daily total + amount <= $5000");
    }

    @Test
    void testCheckLimit_DailyExceeded() {
        CustomerDailyWeeklyStats stats = new CustomerDailyWeeklyStats();
        stats.setDailyTotal(new BigDecimal("4950.00")); // $4950
        LimitCheckContext context = new LimitCheckContext(
                "customer1",
                new BigDecimal("100.00"), // $100
                Instant.now(),
                stats
        );
        // 4950 + 100 = 5050 which is > 5000
        assertThrows(DailyLimitExceededException.class,
                () -> dailyStrategy.checkLimit(context),
                "Should throw DailyLimitExceededException if daily total + amount > $5000");
    }
}

