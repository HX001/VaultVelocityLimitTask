package com.coding.vaulthometask.strategy;

import com.coding.vaulthometask.exception.DailyCountExceededException;
import com.coding.vaulthometask.model.CustomerDailyWeeklyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class DailyCountCheckStrategyTest {

    private DailyCountCheckStrategy countStrategy;

    @BeforeEach
    void setUp() {
        countStrategy = new DailyCountCheckStrategy();
    }

    @Test
    void testCheckLimit_CountNotExceeded() {
        CustomerDailyWeeklyStats stats = new CustomerDailyWeeklyStats();
        stats.setDailyCount(2); // so adding 1 more -> 3 <= 3
        stats.setDailyTotal(BigDecimal.ZERO);
        LimitCheckContext context = new LimitCheckContext(
                "customer3",
                new BigDecimal("100.00"),
                Instant.now(),
                stats
        );

        assertDoesNotThrow(() -> countStrategy.checkLimit(context),
                "No exception when dailyCount + 1 <= 3");
    }

    @Test
    void testCheckLimit_CountExceeded() {
        CustomerDailyWeeklyStats stats = new CustomerDailyWeeklyStats();
        stats.setDailyCount(3); // 3 + 1 -> 4 > 3
        stats.setDailyTotal(BigDecimal.ZERO);
        LimitCheckContext context = new LimitCheckContext(
                "customer3",
                new BigDecimal("10.00"),
                Instant.now(),
                stats
        );

        assertThrows(DailyCountExceededException.class,
                () -> countStrategy.checkLimit(context),
                "Should throw DailyCountExceededException if dailyCount + 1 > 3");
    }
}

