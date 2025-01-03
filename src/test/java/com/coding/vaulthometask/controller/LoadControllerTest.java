package com.coding.vaulthometask.controller;

import com.coding.vaulthometask.exception.DailyLimitExceededException;
import com.coding.vaulthometask.exception.DuplicateRequestException;
import com.coding.vaulthometask.model.VelocityLimitResponse;
import com.coding.vaulthometask.service.LoadService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoadController.class)
public class LoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoadService loadService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public LoadService mockLoadService() {
            return Mockito.mock(LoadService.class);
        }
    }

    @Test
    void testLoad_success() throws Exception {
        VelocityLimitResponse mockResponse = new VelocityLimitResponse("123", "cust1", true);
        Mockito.when(loadService.processLoad(eq("123"), eq("cust1"), eq("$100.00"), any(Instant.class)))
                .thenReturn(mockResponse);


        mockMvc.perform(post("/api/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "id": "123",
                  "customer_id": "cust1",
                  "load_amount": "$100.00",
                  "time": "2025-01-01T00:00:00Z"
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.customer_id").value("cust1"))
                .andExpect(jsonPath("$.accepted").value(true));
    }

    @Test
    void testLoad_duplicateRequest() throws Exception {
        Mockito.when(loadService.processLoad(anyString(), anyString(), anyString(), any(Instant.class)))
                .thenThrow(new DuplicateRequestException("Duplicate request"));

        mockMvc.perform(post("/api/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "id": "456",
                  "customer_id": "cust2",
                  "load_amount": "$300.00",
                  "time": "2025-01-01T00:00:00Z"
                }
                """))
                .andExpect(status().isConflict()) // expecting 409
                .andExpect(jsonPath("$.error").value("duplicate_request"))
                .andExpect(jsonPath("$.message").value("Duplicate request"));
    }

    @Test
    void testLoad_dailyLimitExceeded() throws Exception {
        Mockito.when(loadService.processLoad(anyString(), anyString(), anyString(), any(Instant.class)))
                .thenThrow(new DailyLimitExceededException("Daily limit exceeded"));

        mockMvc.perform(post("/api/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "id": "789",
                  "customer_id": "cust3",
                  "load_amount": "$6000.00",
                  "time": "2025-01-01T00:00:00Z"
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("daily_limit_exceeded"))
                .andExpect(jsonPath("$.message").value("Daily limit exceeded"));
    }

    @Test
    void testLoad_missingFields() throws Exception {
        mockMvc.perform(post("/api/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "id": "999",
                  "load_amount": "$100.00",
                  "time": "2025-01-01T00:00:00Z"
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("illegal_argument"))
                .andExpect(jsonPath("$.message").value("Missing mandatory field(s) in the request body"));
    }
}

