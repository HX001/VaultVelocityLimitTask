package com.coding.vaulthometask.strategy;

public interface LimitCheckStrategy {
    /**
     * @param context includes load amount, time, customer stats
     */
    void checkLimit(LimitCheckContext context);
}