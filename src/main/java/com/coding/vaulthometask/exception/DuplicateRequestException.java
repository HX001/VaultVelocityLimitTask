package com.coding.vaulthometask.exception;

/**
 * duplicate request with same (LoadId, CustomerId)
 */
public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException(String message) {
        super(message);
    }
}
