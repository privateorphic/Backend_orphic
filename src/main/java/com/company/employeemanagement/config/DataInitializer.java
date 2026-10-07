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
            // Create or update default departments
            List<String[]> targetDepts = List.of(
                    new String[]{"Search Engine Optimisation (SEO)", "Search Engine Optimisation Department"},
                    new String[]{"Video Editing", "Video Editing and Post Production"},
                    new String[]{"Social Media", "Social Media Marketing & Operations"},
                    new String[]{"Graphic Design", "Graphic Design and Visual Media"},
                    new String[]{"Digital Marketing", "Digital Marketing & Advertising"},
                    new String[]{"Human Resource (HR)", "Human Resource and People Operations"}
            );

            for (String[] deptData : targetDepts) {
                try {
                    if (!departmentRepository.existsByName(deptData[0])) {
                        departmentRepository.save(Department.builder()
                                .name(deptData[0])
                                .description(deptData[1])
                                .build());
                    }
                } catch (Exception e) {
                    log.warn("Department initialization skipped for {}: {}", deptData[0], e.getMessage());
                }
            }

            // Create default ADMIN user if none exists
            if (!userRepository.existsByEmployeeId("orphic2026") && !userRepository.existsByEmail("privateorphic@gmail.com")) {
                try {
                    Department hrDept = departmentRepository.findByName("Human Resource (HR)")
                            .orElseGet(() -> departmentRepository.findByName("Human Resources").orElse(null));
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
                } catch (Exception e) {
                    log.warn("ADMIN user initialization skipped: {}", e.getMessage());
                }
            }

            // Create default HR user if none exists
            if (!userRepository.existsByEmployeeId("orphichr2026") && !userRepository.existsByEmail("privateorphichr@gmail.com")) {
                try {
                    Department hrDept = departmentRepository.findByName("Human Resource (HR)")
                            .orElseGet(() -> departmentRepository.findByName("Human Resources").orElse(null));
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
                } catch (Exception e) {
                    log.warn("HR user initialization skipped: {}", e.getMessage());
                }
            }
        };
    }
}
