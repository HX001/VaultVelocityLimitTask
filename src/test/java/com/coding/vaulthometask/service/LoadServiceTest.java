package com.coding.vaulthometask.service;


import com.coding.vaulthometask.exception.DuplicateRequestException;
import com.coding.vaulthometask.exception.DailyLimitExceededException;
import com.coding.vaulthometask.model.CustomerDailyWeeklyStats;
import com.coding.vaulthometask.model.LoadAttempt;
import com.coding.vaulthometask.model.VelocityLimitResponse;
import com.coding.vaulthometask.repository.LoadAttemptRepository;
import com.coding.vaulthometask.repository.CustomerStatsRepository;
import com.coding.vaulthometask.strategy.CompositeLimitCheckStrategy;
import com.coding.vaulthometask.strategy.LimitCheckContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

public class LoadServiceTest {

    @Mock
    private LoadAttemptRepository loadAttemptRepository;

    @Mock
    private CustomerStatsRepository statsRepository;

    @Mock
    private CompositeLimitCheckStrategy compositeStrategy;

    @InjectMocks
    private LoadService loadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessLoad_duplicateRequest() {
        // Given
        String loadId = "111";
        String customerId = "custA";
        when(loadAttemptRepository.findByLoadIdAndCustomerId(loadId, customerId))
                .thenReturn(Optional.of(new LoadAttempt()));

        assertThrows(DuplicateRequestException.class, () ->
                loadService.processLoad(loadId, customerId, "$100.00", Instant.now())
        );
        // Verify no new attempt is saved
        verify(loadAttemptRepository, never()).save(any());
    }

    @Test
    void testProcessLoad_compositeThrowsDailyLimit() {
        String loadId = "222";
        String customerId = "custB";
        when(loadAttemptRepository.findByLoadIdAndCustomerId(loadId, customerId))
                .thenReturn(Optional.empty());

        CustomerDailyWeeklyStats stats = new CustomerDailyWeeklyStats();
        stats.setCustomerId(customerId);
        stats.setCurrentDay(LocalDate.now());
        stats.setDailyTotal(BigDecimal.ZERO);
        stats.setDailyCount(0);
        stats.setWeeklyTotal(BigDecimal.ZERO);

        when(statsRepository.findByCustomerId(customerId))
                .thenReturn(Optional.of(stats));

        doThrow(new DailyLimitExceededException("Daily limit exceeded"))
                .when(compositeStrategy).checkLimit(any(LimitCheckContext.class));

        assertThrows(DailyLimitExceededException.class, () ->
                loadService.processLoad(loadId, customerId, "$6000.00", Instant.now())
        );

        // Make sure the stats do NOT get updated in case of an exception
        verify(statsRepository, never()).save(argThat(s -> s.getDailyCount() == 1));
    }

    @Test
    void testProcessLoad_success() {
        String loadId = "333";
        String customerId = "custC";
        when(loadAttemptRepository.findByLoadIdAndCustomerId(loadId, customerId))
                .thenReturn(Optional.empty());

        CustomerDailyWeeklyStats stats = new CustomerDailyWeeklyStats();
        stats.setCustomerId(customerId);
        stats.setCurrentDay(LocalDate.now());
        stats.setDailyTotal(BigDecimal.ZERO);
        stats.setDailyCount(0);
        stats.setWeeklyTotal(BigDecimal.ZERO);

        when(statsRepository.findByCustomerId(customerId))
                .thenReturn(Optional.of(stats));

        // No exception thrown
        doNothing().when(compositeStrategy).checkLimit(any(LimitCheckContext.class));

        VelocityLimitResponse response = loadService.processLoad(
                loadId, customerId, "$100.50", Instant.now()
        );

        assertNotNull(response);
        assertTrue(response.isAccepted());
        assertEquals(loadId, response.getId());
        assertEquals(customerId, response.getCustomer_id());

        // Check if stats were updated
        assertEquals(new BigDecimal("100.50"), stats.getDailyTotal());
        assertEquals(1, stats.getDailyCount());
        assertEquals(new BigDecimal("100.50"), stats.getWeeklyTotal());

        // Verify statsRepository.save(stats) was called
        verify(statsRepository, times(1)).save(stats);
    }
}

