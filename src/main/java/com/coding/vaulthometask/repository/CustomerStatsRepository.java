package com.coding.vaulthometask.repository;

import com.coding.vaulthometask.model.CustomerDailyWeeklyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerStatsRepository extends JpaRepository<CustomerDailyWeeklyStats, Long> {
    Optional<CustomerDailyWeeklyStats> findByCustomerId(String customerId);
}
