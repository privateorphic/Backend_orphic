package com.company.employeemanagement.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DbSchemaPatcher implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Executing MySQL DB Schema patch for leave_requests.leave_type column...");

        // Try modifying to VARCHAR(50)
        try {
            jdbcTemplate.execute("ALTER TABLE leave_requests MODIFY COLUMN leave_type VARCHAR(50) NOT NULL");
            log.info("ALTER TABLE leave_requests MODIFY COLUMN leave_type VARCHAR(50) NOT NULL succeeded.");
        } catch (Exception e) {
            log.error("Failed to alter leave_type to VARCHAR(50): {}", e.getMessage());
        }

        // Try modifying ENUM if table column is MySQL ENUM
        try {
            jdbcTemplate.execute("ALTER TABLE leave_requests MODIFY COLUMN leave_type ENUM('CASUAL', 'SICK', 'EARNED', 'UNPAID', 'WORK_FROM_HOME', 'OTHER') NOT NULL");
            log.info("ALTER TABLE leave_requests MODIFY COLUMN leave_type ENUM(...) succeeded.");
        } catch (Exception e) {
            log.debug("ENUM alter notice: {}", e.getMessage());
        }
    }
}
