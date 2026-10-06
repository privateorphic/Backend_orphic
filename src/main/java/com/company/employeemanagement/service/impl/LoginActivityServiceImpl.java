package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.attendance.LoginActivityResponse;
import com.company.employeemanagement.entity.LoginActivity;
import com.company.employeemanagement.entity.User;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.mapper.LoginActivityMapper;
import com.company.employeemanagement.repository.LoginActivityRepository;
import com.company.employeemanagement.repository.UserRepository;
import com.company.employeemanagement.service.LoginActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginActivityServiceImpl implements LoginActivityService {

    private final LoginActivityRepository loginActivityRepository;
    private final UserRepository userRepository;
    private final LoginActivityMapper loginActivityMapper;
    private final com.company.employeemanagement.security.SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<LoginActivityResponse> getMyLoginActivity(Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        return loginActivityRepository.findByUserId(user.getId(), pageable).map(loginActivityMapper::toLoginActivityResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoginActivityResponse> getAllLoginActivity(Pageable pageable) {
        return loginActivityRepository.findAll(pageable).map(loginActivityMapper::toLoginActivityResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginActivityResponse> getTodayLoginActivity() {
        return loginActivityRepository.findTodayActivity(LocalDate.now()).stream()
                .map(loginActivityMapper::toLoginActivityResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoginActivityResponse> getEmployeeLoginActivity(String employeeId, Pageable pageable) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return loginActivityRepository.findByUserId(user.getId(), pageable).map(loginActivityMapper::toLoginActivityResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoginActivityResponse> getLoginActivityByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        return loginActivityRepository.findByDateRange(start, end, pageable).map(loginActivityMapper::toLoginActivityResponse);
    }
}
