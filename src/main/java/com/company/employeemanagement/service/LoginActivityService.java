package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.attendance.LoginActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LoginActivityService {
    Page<LoginActivityResponse> getAllLoginActivity(Pageable pageable);
    List<LoginActivityResponse> getTodayLoginActivity();
    Page<LoginActivityResponse> getEmployeeLoginActivity(String employeeId, Pageable pageable);
    Page<LoginActivityResponse> getLoginActivityByDateRange(LocalDate start, LocalDate end, Pageable pageable);
    Page<LoginActivityResponse> getMyLoginActivity(Pageable pageable);
}
