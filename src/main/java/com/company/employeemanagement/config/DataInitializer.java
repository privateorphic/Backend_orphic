package com.company.employeemanagement.config;

import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            // Create default departments if none exist
            if (departmentRepository.count() == 0) {
                List<Department> departments = List.of(
                        Department.builder().name("Engineering").description("Software Engineering Department").build(),
                        Department.builder().name("Human Resources").description("HR Department").build(),
                        Department.builder().name("Marketing").description("Marketing Department").build(),
                        Department.builder().name("Finance").description("Finance Department").build(),
                        Department.builder().name("Operations").description("Operations Department").build()
                );
                departmentRepository.saveAll(departments);
                log.info("Default departments created.");
            }

            // Create default ADMIN user if none exists
            if (!userRepository.existsByEmployeeId("orphic2026")) {
                Department hrDept = departmentRepository.findByName("Human Resources").orElse(null);
                User admin = User.builder()
                        .employeeId("orphic2026")
                        .name("System Administrator")
                        .email("privateorphic@gmail.com")
                        .password(passwordEncoder.encode("orphic@2026"))
                        .role(Role.ADMIN)
                        .department(hrDept)
                        .jobTitle("System Administrator")
                        .joiningDate(LocalDate.now())
                        .employmentType(EmploymentType.FULL_TIME)
                        .status(UserStatus.ACTIVE)
                        .build();
                userRepository.save(admin);
                log.info("Default ADMIN user created: privateorphic@gmail.com (orphic2026) / orphic@2026");
            }

            // Create default HR user if none exists
            if (!userRepository.existsByEmployeeId("orphichr2026")) {
                Department hrDept = departmentRepository.findByName("Human Resources").orElse(null);
                User hr = User.builder()
                        .employeeId("orphichr2026")
                        .name("HR Manager")
                        .email("privateorphichr@gmail.com")
                        .password(passwordEncoder.encode("Hr@2026"))
                        .role(Role.HR)
                        .department(hrDept)
                        .jobTitle("HR Manager")
                        .joiningDate(LocalDate.now())
                        .employmentType(EmploymentType.FULL_TIME)
                        .status(UserStatus.ACTIVE)
                        .build();
                userRepository.save(hr);
                log.info("Default HR user created: privateorphichr@gmail.com (orphichr2026) / Hr@2026");
            }
        };
    }
}
