package com.coding.vaulthometask.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleDailyLimitExceeded() {
        DailyLimitExceededException ex = new DailyLimitExceededException("Daily limit exceeded");
        ResponseEntity<ApiErrorResponse> response = handler.handleDailyLimitExceeded(ex);

        assertEquals(400, response.getStatusCode().value());
        ApiErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("daily_limit_exceeded", body.getError());
        assertEquals("Daily limit exceeded", body.getMessage());
        assertEquals(400, body.getStatus().intValue());
    }

    @Test
    void testHandleDuplicateRequest() {
        DuplicateRequestException ex = new DuplicateRequestException("Duplicate request");
        ResponseEntity<ApiErrorResponse> response = handler.handleDuplicateRequest(ex);

        assertEquals(409, response.getStatusCode().value());
        ApiErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("duplicate_request", body.getError());
        assertEquals("Duplicate request", body.getMessage());
        assertEquals(409, body.getStatus().intValue());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Unknown error");
        ResponseEntity<ApiErrorResponse> response = handler.handleAll(ex);

        assertEquals(500, response.getStatusCode().value());
        ApiErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("internal_error", body.getError());
        assertTrue(body.getMessage().contains("Unknown error"));
        assertEquals(500, body.getStatus().intValue());
    }
}

