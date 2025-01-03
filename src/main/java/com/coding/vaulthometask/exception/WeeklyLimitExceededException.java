package com.coding.vaulthometask.exception;

/**
 * Customer operation exceed the weekly amount limit
 */
public class WeeklyLimitExceededException extends RuntimeException {
    public WeeklyLimitExceededException(String message) {
        super(message);
    }
}
