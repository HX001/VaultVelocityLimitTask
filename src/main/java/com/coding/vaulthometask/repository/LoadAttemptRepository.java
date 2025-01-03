package com.coding.vaulthometask.repository;

import com.coding.vaulthometask.model.LoadAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoadAttemptRepository extends JpaRepository<LoadAttempt, Long> {
    Optional<LoadAttempt> findByLoadIdAndCustomerId(String loadId, String customerId);
}
