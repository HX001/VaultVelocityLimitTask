package com.coding.vaulthometask.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompositeLimitCheckStrategy implements LimitCheckStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeLimitCheckStrategy.class);
    private final List<LimitCheckStrategy> strategies;

    public CompositeLimitCheckStrategy(List<LimitCheckStrategy> strategies) {
        this.strategies = strategies.stream()
                .filter(s -> !(s instanceof CompositeLimitCheckStrategy))
                .collect(Collectors.toList());
    }

    @Override
    public void checkLimit(LimitCheckContext context) {
        for (LimitCheckStrategy strategy : strategies) {
            strategy.checkLimit(context);
        }
    }
}

