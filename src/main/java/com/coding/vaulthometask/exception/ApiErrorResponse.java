package com.coding.vaulthometask.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiErrorResponse {
    private String error;      // "duplicate_request", "limit_exceeded"
    private String message;
    private Integer status;    // HTTP code，ex 400, 409, 500
}