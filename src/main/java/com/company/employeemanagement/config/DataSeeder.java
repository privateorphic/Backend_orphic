package com.company.employeemanagement.config;

import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        fixSchemaConstraints();
        seedDepartments();
        seedUsers();
    }

    private void fixSchemaConstraints() {
        String[] alterQueries = {
            "ALTER TABLE notifications MODIFY COLUMN recipient_id BIGINT NULL",
            "ALTER TABLE leave_requests MODIFY COLUMN employee_id BIGINT NULL",
            "ALTER TABLE daily_work MODIFY COLUMN employee_id BIGINT NULL",
            "ALTER TABLE login_activities MODIFY COLUMN employee_id BIGINT NULL",
            "ALTER TABLE tasks MODIFY COLUMN assigned_to_id BIGINT NULL",
            "ALTER TABLE tasks MODIFY COLUMN user_id BIGINT NULL",
            "ALTER TABLE audit_logs MODIFY COLUMN action VARCHAR(100) NOT NULL",
            "ALTER TABLE daily_work ADD COLUMN report_file_name VARCHAR(255) NULL",
            "ALTER TABLE daily_work ADD COLUMN drive_link VARCHAR(500) NULL",
            "UPDATE departments SET name = 'Search Engine Optimisation (SEO)' WHERE name = 'Engineering'",
            "UPDATE departments SET name = 'Human Resource (HR)' WHERE name = 'Human Resources'",
            "UPDATE departments SET name = 'Video Editing' WHERE name = 'Finance'",
            "UPDATE departments SET name = 'Social Media' WHERE name = 'Operations'",
            "UPDATE departments SET name = 'Digital Marketing' WHERE name = 'Marketing'"
        };
        for (String query : alterQueries) {
            try {
                jdbcTemplate.execute(query);
                log.info("✅ Applied schema fix query: {}", query);
            } catch (Exception e) {
                // Column may not exist or already nullable
                log.debug("Skipping schema query: {} -> {}", query, e.getMessage());
            }
        }
    }

    private void seedDepartments() {
        String[][] departments = {
                {"Search Engine Optimisation (SEO)", "Search Engine Optimisation Department"},
                {"Video Editing", "Video Editing and Post Production"},
                {"Social Media", "Social Media Marketing & Operations"},
                {"Graphic Design", "Graphic Design and Visual Media"},
                {"Digital Marketing", "Digital Marketing & Advertising"},
                {"Human Resource (HR)", "Human Resource and People Operations"}
        };

        for (String[] dept : departments) {
            if (!departmentRepository.existsByName(dept[0])) {
                departmentRepository.save(Department.builder()
                        .name(dept[0])
                        .description(dept[1])
                        .build());
            }
        }
        log.info("✅ Departments seeded/synced");
    }

    private void seedUsers() {
        // Seed ADMIN
        if (!userRepository.existsByEmployeeId("ADMIN001")) {
            Department hrDept = departmentRepository.findByName("Human Resource (HR)")
                    .orElseGet(() -> departmentRepository.findByName("Human Resources").orElse(null));
            User admin = User.builder()
                    .employeeId("ADMIN001")
                    .name("System Administrator")
                    .email("admin@company.com")
                    .password(passwordEncoder.encode("Admin@12345"))
                    .role(Role.ADMIN)
                    .department(hrDept)
                    .jobTitle("System Administrator")
                    .joiningDate(LocalDate.of(2023, 1, 1))
                    .employmentType(EmploymentType.FULL_TIME)
                    .status(UserStatus.ACTIVE)
                    .phone("9000000001")
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin seeded: ADMIN001 / Admin@12345");
        }

        // Seed HR
        if (!userRepository.existsByEmployeeId("HR001")) {
            Department hr = departmentRepository.findByName("Human Resource (HR)")
                    .orElseGet(() -> departmentRepository.findByName("Human Resources").orElse(null));
            User hrUser = User.builder()
                    .employeeId("HR001")
                    .name("HR Manager")
                    .email("hr@company.com")
                    .password(passwordEncoder.encode("Hr@12345"))
                    .role(Role.HR)
                    .department(hr)
                    .jobTitle("HR Manager")
                    .joiningDate(LocalDate.of(2023, 3, 1))
                    .employmentType(EmploymentType.FULL_TIME)
                    .status(UserStatus.ACTIVE)
                    .phone("9000000002")
                    .build();
            userRepository.save(hrUser);
            log.info("✅ HR seeded: HR001 / Hr@12345");
        }

        // Seed EMPLOYEE
        if (!userRepository.existsByEmployeeId("EMP001")) {
            Department seoDept = departmentRepository.findByName("Search Engine Optimisation (SEO)").orElse(null);
            User emp = User.builder()
                    .employeeId("EMP001")
                    .name("Rahul Sharma")
                    .email("emp@company.com")
                    .password(passwordEncoder.encode("Emp@12345"))
                    .role(Role.EMPLOYEE)
                    .department(seoDept)
                    .jobTitle("SEO Specialist")
                    .joiningDate(LocalDate.of(2024, 1, 15))
                    .employmentType(EmploymentType.FULL_TIME)
                    .status(UserStatus.ACTIVE)
                    .phone("9000000003")
                    .build();
            userRepository.save(emp);
            log.info("✅ Employee seeded: EMP001 / Emp@12345");
        }
    }
}
