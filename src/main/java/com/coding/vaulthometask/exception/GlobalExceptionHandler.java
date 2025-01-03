package com.coding.vaulthometask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handle DuplicateRequestException，return HTTP 409 Conflict
     */
    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateRequest(DuplicateRequestException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "duplicate_request",
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleDailyLimitExceeded(DailyLimitExceededException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "daily_limit_exceeded",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(WeeklyLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleWeeklyLimitExceeded(WeeklyLimitExceededException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "weekly_limit_exceeded",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DailyCountExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleDailyCountExceeded(DailyCountExceededException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "daily_count_exceeded",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "illegal_argument",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle other exception, return HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAll(Exception ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "internal_error",
                "Internal Server error: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

