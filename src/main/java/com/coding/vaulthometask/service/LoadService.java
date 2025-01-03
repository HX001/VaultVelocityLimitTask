package com.coding.vaulthometask.service;

import com.coding.vaulthometask.exception.DuplicateRequestException;
import com.coding.vaulthometask.model.CustomerDailyWeeklyStats;
import com.coding.vaulthometask.model.LoadAttempt;
import com.coding.vaulthometask.model.VelocityLimitResponse;
import com.coding.vaulthometask.repository.CustomerStatsRepository;
import com.coding.vaulthometask.repository.LoadAttemptRepository;
import com.coding.vaulthometask.strategy.CompositeLimitCheckStrategy;
import com.coding.vaulthometask.strategy.LimitCheckContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class LoadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadService.class);

    private final LoadAttemptRepository loadAttemptRepository;
    private final CustomerStatsRepository statsRepository;
    private final CompositeLimitCheckStrategy compositeStrategy;

    @Autowired
    public LoadService(
            LoadAttemptRepository loadAttemptRepository,
            CustomerStatsRepository statsRepository,
            CompositeLimitCheckStrategy compositeStrategy
    ) {
        this.loadAttemptRepository = loadAttemptRepository;
        this.statsRepository = statsRepository;
        this.compositeStrategy = compositeStrategy;
    }

    public VelocityLimitResponse processLoad(
            String loadId,
            String customerId,
            String loadAmountStr,
            Instant time
    ) {
        // 1. Check if there is a duplicated load record
        Optional<LoadAttempt> existing = loadAttemptRepository.findByLoadIdAndCustomerId(loadId, customerId);
        if (existing.isPresent()) {
            LOGGER.warn("Declined load due to duplication. loadId = {}, customerId = {}", loadId, customerId);
            throw new DuplicateRequestException(
                    String.format("Load duplicate: loadId = %s, customerId = %s", loadId, customerId)
            );
        }

        // 2. Convert amount
        BigDecimal loadAmount = parseAmount(loadAmountStr);

        // 3. Save load record
        LoadAttempt loadAttempt = new LoadAttempt(loadId, customerId, loadAmount, time);
        loadAttemptRepository.save(loadAttempt);

        // 4. Get or create record for this user，check and reset daily or weekly record (if crossing days/weeks)
        CustomerDailyWeeklyStats stats = getOrCreateStats(customerId);
        resetStatsIfNewDayOrWeek(stats, time);

        // 5. check limit
        LimitCheckContext context = new LimitCheckContext(customerId, loadAmount, time, stats);
        compositeStrategy.checkLimit(context);

        // 6. if passed, save record
        stats.setDailyTotal(stats.getDailyTotal().add(loadAmount));
        stats.setDailyCount(stats.getDailyCount() + 1);
        stats.setWeeklyTotal(stats.getWeeklyTotal().add(loadAmount));
        statsRepository.save(stats);
        LOGGER.info("Accepted load. loadId = {}, customerId = {}, dailyTotal = {}, weeklyTotal = {}",
                loadId, customerId, stats.getDailyTotal(), stats.getWeeklyTotal());

        // 7. return response
        return new VelocityLimitResponse(loadId, customerId, true);
    }

    private BigDecimal parseAmount(String amountStr) {
        // "$1234.56" -> "1234.56"
        if (amountStr.startsWith("$")) {
            amountStr = amountStr.substring(1);
        }
        return new BigDecimal(amountStr);
    }

    private CustomerDailyWeeklyStats getOrCreateStats(String customerId) {
        return statsRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    CustomerDailyWeeklyStats s = new CustomerDailyWeeklyStats();
                    s.setCustomerId(customerId);
                    s.setCurrentDay(LocalDate.now(ZoneOffset.UTC));
                    s.setDailyTotal(BigDecimal.ZERO);
                    s.setDailyCount(0);
                    LocalDate today = LocalDate.now(ZoneOffset.UTC);
                    LocalDate monday = getMondayOfWeek(today);
                    s.setCurrentWeekMonday(monday);
                    s.setWeeklyTotal(BigDecimal.ZERO);
                    return s;
                });
    }

    private void resetStatsIfNewDayOrWeek(CustomerDailyWeeklyStats stats, Instant loadTime) {
        LocalDate loadDate = loadTime.atZone(ZoneOffset.UTC).toLocalDate();

        // reset if new day
        if (!loadDate.equals(stats.getCurrentDay())) {
            stats.setCurrentDay(loadDate);
            stats.setDailyTotal(BigDecimal.ZERO);
            stats.setDailyCount(0);
            LOGGER.info("Resetting stats for customer = {}, new day = {}",
                    stats.getCustomerId(), loadDate);
        }

        // reset if new week
        LocalDate monday = getMondayOfWeek(loadDate);
        if (!monday.equals(stats.getCurrentWeekMonday())) {
            stats.setCurrentWeekMonday(monday);
            stats.setWeeklyTotal(BigDecimal.ZERO);
            LOGGER.info("Resetting stats for customer = {}, new day = {}, new week = {}",
                    stats.getCustomerId(), loadDate, monday);
        }
    }

    private LocalDate getMondayOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }
}

