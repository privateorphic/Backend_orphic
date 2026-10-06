package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.auth.LoginRequest;
import com.company.employeemanagement.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request, String ipAddress);
    void logout();
}
