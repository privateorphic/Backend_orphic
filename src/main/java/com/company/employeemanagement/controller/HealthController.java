package com.company.employeemanagement.controller;

import com.company.employeemanagement.exception.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check endpoint")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @Operation(summary = "Application health check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("status", "UP");
        info.put("timestamp", LocalDateTime.now().toString());
        info.put("application", "Employee Management System");
        info.put("version", "1.0.0");

        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            info.put("database", "CONNECTED");
        } catch (Exception e) {
            info.put("database", "DISCONNECTED");
            info.put("databaseError", e.getMessage());
        }

        return ResponseEntity.ok(ApiResponse.success(info, "System is healthy"));
    }
}
