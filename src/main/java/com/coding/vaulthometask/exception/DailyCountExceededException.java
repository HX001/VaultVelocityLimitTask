package com.coding.vaulthometask.exception;

/**
 * Customer operation exceed the daily count limit
 */
public class DailyCountExceededException extends RuntimeException {
    public DailyCountExceededException(String message) {
        super(message);
    }
}
