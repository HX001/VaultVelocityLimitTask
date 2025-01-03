package com.coding.vaulthometask.strategy;

import com.coding.vaulthometask.exception.DailyCountExceededException;
import com.coding.vaulthometask.exception.DailyLimitExceededException;
import com.coding.vaulthometask.exception.WeeklyLimitExceededException;
import com.coding.vaulthometask.model.CustomerDailyWeeklyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompositeLimitCheckStrategyTest {

    @Mock
    private LimitCheckStrategy dailyStrategy;
    @Mock
    private LimitCheckStrategy weeklyStrategy;
    @Mock
    private LimitCheckStrategy countStrategy;

    private CompositeLimitCheckStrategy composite;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        composite = new CompositeLimitCheckStrategy(
                List.of(dailyStrategy, weeklyStrategy, countStrategy)
        );
    }

    @Test
    void testCheckLimit_AllPass() {
        doNothing().when(dailyStrategy).checkLimit(any());
        doNothing().when(weeklyStrategy).checkLimit(any());
        doNothing().when(countStrategy).checkLimit(any());

        LimitCheckContext context = new LimitCheckContext(
                "custX",
                new BigDecimal("100.00"),
                Instant.now(),
                new CustomerDailyWeeklyStats()
        );
        assertDoesNotThrow(() -> composite.checkLimit(context),
                "No exception if all sub-strategies pass");
    }

    @Test
    void testCheckLimit_DailyLimitThrowsFirst() {
        doThrow(new DailyLimitExceededException("Daily limit exceeded"))
                .when(dailyStrategy).checkLimit(any());

        // The others won't be called if the first throws
        doNothing().when(weeklyStrategy).checkLimit(any());
        doNothing().when(countStrategy).checkLimit(any());

        LimitCheckContext context = new LimitCheckContext(
                "custY",
                new BigDecimal("6000.00"),
                Instant.now(),
                new CustomerDailyWeeklyStats()
        );
        assertThrows(DailyLimitExceededException.class,
                () -> composite.checkLimit(context));

        // Verify that weeklyStrategy and countStrategy are NOT called
        verify(weeklyStrategy, never()).checkLimit(any());
        verify(countStrategy, never()).checkLimit(any());
    }

    @Test
    void testCheckLimit_WeeklyLimitThrowsSecond() {
        // daily passes, weekly throws, count not called
        doNothing().when(dailyStrategy).checkLimit(any());
        doThrow(new WeeklyLimitExceededException("Weekly limit exceeded"))
                .when(weeklyStrategy).checkLimit(any());

        doNothing().when(countStrategy).checkLimit(any());

        LimitCheckContext context = new LimitCheckContext(
                "custZ",
                new BigDecimal("25000.00"),
                Instant.now(),
                new CustomerDailyWeeklyStats()
        );
        assertThrows(WeeklyLimitExceededException.class,
                () -> composite.checkLimit(context));

        verify(countStrategy, never()).checkLimit(any());
    }

    @Test
    void testCheckLimit_CountLimitThrowsLast() {
        doNothing().when(dailyStrategy).checkLimit(any());
        doNothing().when(weeklyStrategy).checkLimit(any());
        doThrow(new DailyCountExceededException("Daily count exceeded"))
                .when(countStrategy).checkLimit(any());

        LimitCheckContext context = new LimitCheckContext(
                "custX",
                new BigDecimal("10.00"),
                Instant.now(),
                new CustomerDailyWeeklyStats()
        );
        assertThrows(DailyCountExceededException.class,
                () -> composite.checkLimit(context));

        verify(dailyStrategy, times(1)).checkLimit(any());
        verify(weeklyStrategy, times(1)).checkLimit(any());
        verify(countStrategy, times(1)).checkLimit(any());
    }
}
