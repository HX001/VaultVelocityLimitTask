package com.coding.vaulthometask.exception;

/**
 * Customer operation exceed the daily amount limit
 */
public class DailyLimitExceededException extends RuntimeException {
    public DailyLimitExceededException(String message) {
        super(message);
    }
}
