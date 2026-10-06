package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.employee.CreateEmployeeRequest;
import com.company.employeemanagement.dto.employee.EmployeeResponse;
import com.company.employeemanagement.dto.employee.UpdateEmployeeRequest;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.exception.DuplicateResourceException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.UserRepository;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.AuditLogService;
import com.company.employeemanagement.service.NotificationService;
import com.company.employeemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        // 1. Validate duplicates
        if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new DuplicateResourceException("Employee ID already exists: " + request.getEmployeeId());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        }

        User employee = User.builder()
                .employeeId(request.getEmployeeId())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // BCrypt hash — never plain text
                .phone(request.getPhone())
                .role(Role.EMPLOYEE)
                .department(department)
                .jobTitle(request.getJobTitle())
                .joiningDate(request.getJoiningDate())
                .employmentType(request.getEmploymentType() != null ? request.getEmploymentType() : EmploymentType.FULL_TIME)
                .status(UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(employee);

        User actor = securityUtils.getCurrentUser();
        auditLogService.log(actor, AuditAction.EMPLOYEE_CREATED, "User", saved.getId(),
                "New employee created: " + saved.getEmployeeId());

        notificationService.createNotification(saved, NotificationType.EMPLOYEE_CREATED,
                "Welcome to the team!", "Your account has been created. Employee ID: " + saved.getEmployeeId(), null, null);

        log.info("Employee created: {} by {}", saved.getEmployeeId(), actor.getEmployeeId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(Long id) {
        return mapToResponse(findUserById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return userRepository.findByRole(Role.EMPLOYEE, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
        return userRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        User user = findUserById(id);
        applyUpdate(user, request);
        User saved = userRepository.save(user);
        User actor = securityUtils.getCurrentUser();
        auditLogService.log(actor, AuditAction.EMPLOYEE_UPDATED, "User", saved.getId(), "Employee updated: " + saved.getEmployeeId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void changeEmployeeStatus(Long id, UserStatus status) {
        User user = findUserById(id);
        UserStatus old = user.getStatus();
        user.setStatus(status);
        userRepository.save(user);
        User actor = securityUtils.getCurrentUser();
        auditLogService.log(actor, AuditAction.EMPLOYEE_STATUS_CHANGED, "User", id,
                "Status changed from " + old + " to " + status + " for " + user.getEmployeeId());
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        User user = findUserById(id);
        user.setStatus(UserStatus.TERMINATED);
        userRepository.save(user);
        User actor = securityUtils.getCurrentUser();
        auditLogService.log(actor, AuditAction.EMPLOYEE_STATUS_CHANGED, "User", id, "Employee marked as TERMINATED: " + user.getEmployeeId());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getMyProfile() {
        return mapToResponse(securityUtils.getCurrentUser());
    }

    @Override
    @Transactional
    public EmployeeResponse updateMyProfile(UpdateEmployeeRequest request) {
        User user = securityUtils.getCurrentUser();
        // Employees can only update limited fields (not role, status)
        if (StringUtils.hasText(request.getName())) user.setName(request.getName());
        if (StringUtils.hasText(request.getPhone())) user.setPhone(request.getPhone());
        if (StringUtils.hasText(request.getNewPassword())) user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return mapToResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(com.company.employeemanagement.dto.employee.ChangePasswordRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("Current password does not match");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        auditLogService.log(currentUser, AuditAction.EMPLOYEE_UPDATED, "User", currentUser.getId(), "Password changed successfully");
    }

    private void applyUpdate(User user, UpdateEmployeeRequest request) {
        if (StringUtils.hasText(request.getName())) user.setName(request.getName());
        if (StringUtils.hasText(request.getEmail())) {
            if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) user.setPhone(request.getPhone());
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            user.setDepartment(dept);
        }
        if (StringUtils.hasText(request.getJobTitle())) user.setJobTitle(request.getJobTitle());
        if (request.getJoiningDate() != null) user.setJoiningDate(request.getJoiningDate());
        if (request.getEmploymentType() != null) user.setEmploymentType(request.getEmploymentType());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (StringUtils.hasText(request.getNewPassword())) user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    public EmployeeResponse mapToResponse(User user) {
        return EmployeeResponse.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .jobTitle(user.getJobTitle())
                .joiningDate(user.getJoiningDate())
                .employmentType(user.getEmploymentType())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
