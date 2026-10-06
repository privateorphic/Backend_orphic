package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.auth.LoginRequest;
import com.company.employeemanagement.dto.auth.LoginResponse;
import com.company.employeemanagement.dto.auth.UserInfo;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.LoginActivityRepository;
import com.company.employeemanagement.repository.UserRepository;
import com.company.employeemanagement.security.JwtTokenProvider;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.AuditLogService;
import com.company.employeemanagement.service.AuthService;
import com.company.employeemanagement.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final LoginActivityRepository loginActivityRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        // Support login by email OR employeeId
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Generate JWT
        String token = jwtTokenProvider.generateTokenForEmail(email);

        // Record login activity — always create a NEW record
        Boolean isOffice = request.getIsOfficeLocation() == null || Boolean.TRUE.equals(request.getIsOfficeLocation());
        String locName = isOffice 
                ? "In Office" 
                : (request.getLocationName() != null && !request.getLocationName().isBlank() ? request.getLocationName() : "Outside Office");

        LoginActivity activity = LoginActivity.builder()
                .user(user)
                .loginDate(LocalDate.now())
                .loginTime(LocalTime.now())
                .status(LoginStatus.ACTIVE)
                .ipAddress(ipAddress)
                .isOfficeLocation(isOffice)
                .latitude(isOffice ? null : request.getLatitude())
                .longitude(isOffice ? null : request.getLongitude())
                .locationName(locName)
                .build();
        loginActivityRepository.save(activity);

        // Audit log
        auditLogService.log(user, AuditAction.LOGIN, "User", user.getId(), "User logged in from " + ipAddress);

        log.info("User logged in: {} ({})", user.getEmployeeId(), user.getEmail());

        return LoginResponse.builder()
                .success(true)
                .message("Login successful")
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(UserInfo.builder()
                        .id(user.getId())
                        .employeeId(user.getEmployeeId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                        .jobTitle(user.getJobTitle())
                        .status(user.getStatus().name())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public void logout() {
        User currentUser = securityUtils.getCurrentUser();

        // Find the most recent ACTIVE session — use JWT identity, never frontend-provided ID
        loginActivityRepository.findTopByUserAndStatusOrderByCreatedAtDesc(currentUser, LoginStatus.ACTIVE)
                .ifPresent(activity -> {
                    LocalTime logoutTime = LocalTime.now();
                    activity.setLogoutTime(logoutTime);
                    activity.setSessionDuration(DateUtils.calculateDuration(activity.getLoginTime(), logoutTime));
                    activity.setStatus(LoginStatus.LOGGED_OUT);
                    loginActivityRepository.save(activity);
                    log.info("User logged out: {} — duration: {}", currentUser.getEmployeeId(), activity.getSessionDuration());
                });

        auditLogService.log(currentUser, AuditAction.LOGOUT, "User", currentUser.getId(), "User logged out");
        SecurityContextHolder.clearContext();
    }
}
