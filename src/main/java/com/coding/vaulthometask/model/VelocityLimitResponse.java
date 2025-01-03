package com.coding.vaulthometask.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VelocityLimitResponse {
    private String id;
    private String customer_id;
    private boolean accepted;
}
