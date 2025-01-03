package com.coding.vaulthometask;

import com.coding.vaulthometask.exception.DuplicateRequestException;
import com.coding.vaulthometask.exception.DailyLimitExceededException;
import com.coding.vaulthometask.model.VelocityLimitResponse;
import com.coding.vaulthometask.service.LoadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class FileProcessor {

    @Bean
    public CommandLineRunner processFile(LoadService loadService) {
        return args -> {
            Path inputPath = Path.of("input.txt");
            Path outputPath = Path.of("outputProcess.txt");

            if (!Files.exists(inputPath)) {
                System.out.println("No input.txt found, skipping file processing.");
                return;
            }

            try (BufferedReader reader = Files.newBufferedReader(inputPath);
                 BufferedWriter writer = Files.newBufferedWriter(outputPath)) {

                ObjectMapper mapper = new ObjectMapper();
                String line;
                while ((line = reader.readLine()) != null) {
                    Map<String, String> requestMap = mapper.readValue(line, Map.class);
                    String loadId = requestMap.get("id");
                    String customerId = requestMap.get("customer_id");
                    String loadAmount = requestMap.get("load_amount");
                    Instant time = Instant.parse(requestMap.get("time"));

                    VelocityLimitResponse response;
                    boolean accepted = false;
                    boolean shouldWrite = true;

                    try {
                        response = loadService.processLoad(loadId, customerId, loadAmount, time);
                        if (response != null) {
                            accepted = response.isAccepted();
                        }
                    } catch (DailyLimitExceededException e) {
                        System.out.println("Request exceeded limit for loadId=" + loadId);
                    } catch (DuplicateRequestException e) {
                        System.out.println("Duplicate request detected for loadId=" + loadId);
                        shouldWrite = false;
                    } catch (Exception e) {
                        System.err.println("Unknown error: " + e.getMessage());
                        e.printStackTrace();
                    }

                    if (shouldWrite) {
                        Map<String, Object> outputMap = new LinkedHashMap<>();
                        outputMap.put("id", loadId);
                        outputMap.put("customer_id", customerId);
                        outputMap.put("accepted", accepted);
                        writer.write(mapper.writeValueAsString(outputMap));
                        writer.newLine();
                    }
                }

                System.out.println("Processing completed.");
                writer.flush();
                OutputComparator.compareResult();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}

