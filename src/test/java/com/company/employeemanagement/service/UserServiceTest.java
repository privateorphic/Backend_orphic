package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.employee.CreateEmployeeRequest;
import com.company.employeemanagement.dto.employee.EmployeeResponse;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.exception.DuplicateResourceException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.UserRepository;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SecurityUtils securityUtils;
    @Mock private AuditLogService auditLogService;
    @Mock private NotificationService notificationService;

    @InjectMocks private UserServiceImpl userService;

    private User mockAdmin;
    private CreateEmployeeRequest validRequest;

    @BeforeEach
    void setUp() {
        mockAdmin = User.builder()
                .id(1L).employeeId("ADMIN001").name("Admin").email("admin@company.com")
                .role(Role.ADMIN).status(UserStatus.ACTIVE).build();

        validRequest = new CreateEmployeeRequest();
        validRequest.setEmployeeId("EMP001");
        validRequest.setName("Rahul Sharma");
        validRequest.setEmail("rahul@company.com");
        validRequest.setPassword("Test@12345");
        validRequest.setPhone("9999999999");
        validRequest.setJobTitle("Software Developer");
        validRequest.setJoiningDate(LocalDate.now());
        validRequest.setEmploymentType(EmploymentType.FULL_TIME);
    }

    @Test
    void createEmployee_success() {
        when(userRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(userRepository.existsByEmail("rahul@company.com")).thenReturn(false);
        when(passwordEncoder.encode("Test@12345")).thenReturn("$2a$bcrypt$hash");
        when(securityUtils.getCurrentUser()).thenReturn(mockAdmin);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        EmployeeResponse response = userService.createEmployee(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmployeeId()).isEqualTo("EMP001");
        assertThat(response.getName()).isEqualTo("Rahul Sharma");
        assertThat(response.getRole()).isEqualTo(Role.EMPLOYEE);
        assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);

        verify(passwordEncoder).encode("Test@12345");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createEmployee_duplicateEmployeeId_throwsException() {
        when(userRepository.existsByEmployeeId("EMP001")).thenReturn(true);

        assertThatThrownBy(() -> userService.createEmployee(validRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("EMP001");

        verify(userRepository, never()).save(any());
    }

    @Test
    void createEmployee_duplicateEmail_throwsException() {
        when(userRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(userRepository.existsByEmail("rahul@company.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createEmployee(validRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email");

        verify(userRepository, never()).save(any());
    }

    @Test
    void getEmployee_notFound_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getEmployee(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createEmployee_passwordIsHashed() {
        when(userRepository.existsByEmployeeId(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("Test@12345")).thenReturn("$2a$hashed");
        when(securityUtils.getCurrentUser()).thenReturn(mockAdmin);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        userService.createEmployee(validRequest);

        verify(passwordEncoder).encode("Test@12345");
        // Plain text password must never be stored
        verify(userRepository).save(argThat(u ->
                !u.getPassword().equals("Test@12345") && u.getPassword().equals("$2a$hashed")));
    }
}
