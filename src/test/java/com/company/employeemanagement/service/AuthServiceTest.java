package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.auth.LoginRequest;
import com.company.employeemanagement.dto.auth.LoginResponse;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.repository.LoginActivityRepository;
import com.company.employeemanagement.repository.UserRepository;
import com.company.employeemanagement.security.JwtTokenProvider;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private UserRepository userRepository;
    @Mock private LoginActivityRepository loginActivityRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 86400000L);
        testUser = User.builder()
                .id(1L)
                .employeeId("EMP001")
                .name("Test User")
                .email("test@company.com")
                .password("$2a$bcrypt")
                .role(Role.EMPLOYEE)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void login_success_createsLoginActivity() {
        LoginRequest request = new LoginRequest();
        request.setUsername("EMP001");
        request.setPassword("Test@12345");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@company.com");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail("test@company.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateTokenForEmail("test@company.com")).thenReturn("jwt-token-123");
        when(loginActivityRepository.save(any(LoginActivity.class))).thenAnswer(inv -> {
            LoginActivity la = inv.getArgument(0);
            la.setId(1L);
            return la;
        });

        LoginResponse response = authService.login(request, "127.0.0.1");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getToken()).isEqualTo("jwt-token-123");
        assertThat(response.getUser().getEmployeeId()).isEqualTo("EMP001");

        // Verify login activity was CREATED (not updated)
        verify(loginActivityRepository).save(argThat(la ->
                la.getStatus() == LoginStatus.ACTIVE && la.getLogoutTime() == null));
    }

    @Test
    void login_invalidCredentials_throwsBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("EMP001");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request, "127.0.0.1"))
                .isInstanceOf(BadCredentialsException.class);

        verify(loginActivityRepository, never()).save(any());
    }

    @Test
    void logout_savesLogoutTimeAndDuration() {
        when(securityUtils.getCurrentUser()).thenReturn(testUser);

        LoginActivity activeSession = LoginActivity.builder()
                .id(1L)
                .user(testUser)
                .loginTime(java.time.LocalTime.of(9, 0, 0))
                .status(LoginStatus.ACTIVE)
                .build();

        when(loginActivityRepository.findTopByUserAndStatusOrderByCreatedAtDesc(testUser, LoginStatus.ACTIVE))
                .thenReturn(Optional.of(activeSession));

        authService.logout();

        verify(loginActivityRepository).save(argThat(la ->
                la.getStatus() == LoginStatus.LOGGED_OUT &&
                la.getLogoutTime() != null &&
                la.getSessionDuration() != null));
    }

    @Test
    void logout_noActiveSession_doesNotThrow() {
        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(loginActivityRepository.findTopByUserAndStatusOrderByCreatedAtDesc(testUser, LoginStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Should not throw even if no active session found
        assertThatCode(() -> authService.logout()).doesNotThrowAnyException();
    }

    @Test
    void login_response_doesNotContainPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test@company.com");
        request.setPassword("Test@12345");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@company.com");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail("test@company.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateTokenForEmail(anyString())).thenReturn("token");
        when(loginActivityRepository.save(any())).thenReturn(new LoginActivity());

        LoginResponse response = authService.login(request, "127.0.0.1");

        // UserInfo must not expose password
        assertThat(response.getUser()).isNotNull();
        // UserInfo does not have a password field by design
        assertThat(response.getUser().getClass().getDeclaredFields())
                .noneMatch(f -> f.getName().equalsIgnoreCase("password"));
    }
}
