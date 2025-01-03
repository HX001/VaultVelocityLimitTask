package com.coding.vaulthometask.controller;

import com.coding.vaulthometask.model.VelocityLimitResponse;
import com.coding.vaulthometask.service.LoadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadController.class);

    private final LoadService loadService;

    @Autowired
    public LoadController(LoadService loadService) {
        this.loadService = loadService;
    }

    @PostMapping("/load")
    public ResponseEntity<?> load(@RequestBody Map<String, String> requestBody) {
        String loadId = requestBody.get("id");
        String customerId = requestBody.get("customer_id");
        String loadAmount = requestBody.get("load_amount");
        String timeStr = requestBody.get("time");

        if (loadId == null || customerId == null || loadAmount == null || timeStr == null) {
            LOGGER.warn("Missing mandatory field(s) in the request body");
            throw new IllegalArgumentException("Missing mandatory field(s) in the request body");
        }

        Instant time = Instant.parse(timeStr);

        VelocityLimitResponse response =
                loadService.processLoad(loadId, customerId, loadAmount, time);

        return ResponseEntity.ok(response);
    }
}
